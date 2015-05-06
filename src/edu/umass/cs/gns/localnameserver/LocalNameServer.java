/*
 * Copyright (C) 2015
 * University of Massachusetts
 * All Rights Reserved 
 *
 * Initial developer(s): Westy.
 */
package edu.umass.cs.gns.localnameserver;

import static edu.umass.cs.gns.localnameserver.LNSNodeConfig.INVALID_PING_LATENCY;
import edu.umass.cs.gns.main.GNS;
import edu.umass.cs.gns.nio.AbstractPacketDemultiplexer;
import edu.umass.cs.gns.nio.InterfaceJSONNIOTransport;
import edu.umass.cs.gns.nio.JSONMessageExtractor;
import edu.umass.cs.gns.nio.JSONMessenger;
import edu.umass.cs.gns.nio.JSONNIOTransport;
import edu.umass.cs.gns.nsdesign.Config;
import edu.umass.cs.gns.nsdesign.Shutdownable;
import edu.umass.cs.gns.util.NetworkUtils;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.Logger;

/**
 *
 * @author westy
 */
public class LocalNameServer implements RequestHandlerInterface, Shutdownable {

  public static final int REQUEST_ACTIVES_QUERY_TIMEOUT = 1000; 
  public static final int MAX_QUERY_WAIT_TIME = 4000;
 
  private static final Logger LOG = Logger.getLogger(LocalNameServer.class.getName());
  
  public final static int DEFAULT_LNS_TCP_PORT = 24398;
  
  private static final ConcurrentMap<Integer, LNSRequestInfo> outstandingRequests = new ConcurrentHashMap<>(10, 0.75f, 3);

  private final ScheduledThreadPoolExecutor executorService = new ScheduledThreadPoolExecutor(5);
  
  private final PendingTasks  pendingTasks = new PendingTasks();
   
  private InterfaceJSONNIOTransport tcpTransport;
  private final LNSNodeConfig nodeConfig;
  private final LNSConsistentReconfigurableNodeConfig crnodeConfig;
  private final InetSocketAddress nodeAddress;
  private final AbstractPacketDemultiplexer demultiplexer;
  private boolean debuggingEnabled = true;
  
  public LocalNameServer(InetSocketAddress nodeAddress, LNSNodeConfig nodeConfig) {
    this.nodeAddress = nodeAddress;
    this.nodeConfig = nodeConfig;
    this.crnodeConfig = new LNSConsistentReconfigurableNodeConfig(nodeConfig);
    this.demultiplexer = new LNSPacketDemultiplexer(this);
    try {
      this.tcpTransport = initTransport(demultiplexer);
    } catch (IOException e) {
      LOG.info("Unabled to start LNS listener: " + e);
    }
  }

  private InterfaceJSONNIOTransport initTransport(AbstractPacketDemultiplexer demultiplexer) throws IOException {
    LOG.info("Starting LNS listener on " + nodeAddress);
    JSONNIOTransport gnsNiot = new JSONNIOTransport(nodeAddress, crnodeConfig, new JSONMessageExtractor(demultiplexer));
    new Thread(gnsNiot).start();
    // id is null here because we're the LNS
    return new JSONMessenger<>(gnsNiot);
  }

  @Override
  public void shutdown() {
    tcpTransport.stop();
    demultiplexer.stop();
  }
  
  public static void main(String[] args) {
    try {
    String nodeFilename;
    if (args.length == 1) {
       nodeFilename = args[0];
    } else { // special case for testing
      nodeFilename = Config.WESTY_GNS_DIR_PATH + "/conf/name-server-info";
    }
    InetSocketAddress address = new InetSocketAddress(NetworkUtils.getLocalHostLANAddress().getHostAddress(),
             DEFAULT_LNS_TCP_PORT);
    new LocalNameServer(address, new LNSNodeConfig(nodeFilename));
    } catch (IOException e) {
      System.out.println("Usage: java -cp GNS.jar edu.umass.cs.gns.localnameserver <nodeConfigFile>");
    }    
  }

  @Override
  public InterfaceJSONNIOTransport getTcpTransport() {
    return tcpTransport;
  }

  @Override
  public LNSConsistentReconfigurableNodeConfig getNodeConfig() {
    return crnodeConfig;
  }

  @Override
  public InetSocketAddress getNodeAddress() {
    return nodeAddress;
  }

  @Override
  public AbstractPacketDemultiplexer getDemultiplexer() {
    return demultiplexer;
  }

  @Override
  public boolean isDebugMode() {
    return debuggingEnabled;
  }

  @Override
  public ScheduledThreadPoolExecutor getExecutorService() {
    return executorService;
  }

  @Override
  public void addRequestInfo(int id, LNSRequestInfo requestInfo) {
    outstandingRequests.put(id, requestInfo);
  }

  @Override
  public LNSRequestInfo removeRequestInfo(int id) {
    return outstandingRequests.remove(id);
  }

  @Override
  public LNSRequestInfo getRequestInfo(int id) {
    return outstandingRequests.get(id);
  }

  @Override
  public PendingTasks getPendingTasks() {
    return pendingTasks;
  }   
  
  /**
   * Selects the closest Name Server from a set of Name Servers.
   *
   * @param servers
   * @return id of closest server or INVALID_NAME_SERVER_ID if one can't be found
   */
  @Override
  public InetSocketAddress getClosestServer(Set<InetSocketAddress> servers) {
    return getClosestServer(servers, null);
  }

  /**
   * Selects the closest Name Server from a set of Name Servers.
   * excludeNameServers is a set of Name Servers from the first list to not consider.
   * If the local server is one of the serverIds and not excluded this will return it.
   *
   * @param serverIds
   * @param excludeServers
   * @return id of closest server or null if one can't be found
   */
  @Override
  public InetSocketAddress getClosestServer(Set<InetSocketAddress> serverIds, Set<InetSocketAddress> excludeServers) {
    if (serverIds == null || serverIds.isEmpty()) {
      return null;
    }

    long lowestLatency = Long.MAX_VALUE;
    InetSocketAddress serverAddress = null;
    for (InetSocketAddress serverId : serverIds) {
      if (excludeServers != null && excludeServers.contains(serverId)) {
        continue;
      }
      long pingLatency = nodeConfig.getPingLatency(serverId);
      if (pingLatency != INVALID_PING_LATENCY && pingLatency < lowestLatency) {
        lowestLatency = pingLatency;
        serverAddress = serverId;
      }
    }
    if (Config.debuggingEnabled) {
      GNS.getLogger().info("Closest server is " + serverAddress);
    }
    return serverAddress;
  }
  
}
