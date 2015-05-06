package edu.umass.cs.gns.localnameserver;

import edu.umass.cs.gns.reconfiguration.reconfigurationutils.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;

import edu.umass.cs.gns.reconfiguration.InterfaceModifiableActiveConfig;
import edu.umass.cs.gns.reconfiguration.InterfaceModifiableRCConfig;
import edu.umass.cs.gns.reconfiguration.InterfaceReconfigurableNodeConfig;

/*
 * This class is a wrapper around NodeConfig to ensure that it is consistent,
 * i.e., it returns consistent results even if it changes midway. In particular,
 * it does not allow the use of a method like getNodeIDs().
 * 
 * It also has consistent hashing utility methods.
 */
public class LNSConsistentReconfigurableNodeConfig<NodeIDType> extends
        LNSConsistentNodeConfig<NodeIDType> implements
        InterfaceModifiableActiveConfig<NodeIDType>, InterfaceModifiableRCConfig<NodeIDType> {

  private final SimpleReconfiguratorNodeConfig<NodeIDType> nodeConfig;
  private Set<NodeIDType> activeReplicas; // most recent cached copy
  private Set<NodeIDType> reconfigurators; // most recent cached copy

  // need to refresh when nodeConfig changes
  private final ConsistentHashing<NodeIDType> CH_RC;
  // need to refresh when nodeConfig changes
  private final ConsistentHashing<NodeIDType> CH_AR;

  private Set<NodeIDType> reconfiguratorsSlatedForRemoval = new HashSet<NodeIDType>();

  public LNSConsistentReconfigurableNodeConfig(
          InterfaceReconfigurableNodeConfig<NodeIDType> nc) {
    super(nc);
    this.nodeConfig = new SimpleReconfiguratorNodeConfig<NodeIDType>(nc);
    this.activeReplicas = this.nodeConfig.getActiveReplicas();
    this.reconfigurators = this.nodeConfig.getReconfigurators();
    this.CH_RC = new ConsistentHashing<NodeIDType>(this.reconfigurators);
    this.CH_AR = new ConsistentHashing<NodeIDType>(this.activeReplicas, true);
  }

  @Override
  public Set<NodeIDType> getValuesFromStringSet(Set<String> strNodes) {
    return this.nodeConfig.getValuesFromStringSet(strNodes);
  }

  @Override
  public Set<NodeIDType> getValuesFromJSONArray(JSONArray array)
          throws JSONException {
    return this.nodeConfig.getValuesFromJSONArray(array);
  }

  @Override
  public Set<NodeIDType> getNodeIDs() {
    throw new RuntimeException("The use of this method is not permitted");
  }

  @Override
  public Set<NodeIDType> getActiveReplicas() {
    return this.nodeConfig.getActiveReplicas();
  }

  @Override
  public Set<NodeIDType> getReconfigurators() {
    return this.nodeConfig.getReconfigurators();
  }

  // consistent coz it always consults nodeConfig
  public ArrayList<InetAddress> getNodeIPs(Set<NodeIDType> nodeIDs) {
    ArrayList<InetAddress> addresses = new ArrayList<InetAddress>();
    for (NodeIDType id : nodeIDs) {
      addresses.add(this.nodeConfig.getNodeAddress(id));
    }
    assert (addresses != null);
    return addresses;
  }

  // refresh before returning
  public Set<NodeIDType> getReplicatedReconfigurators(String name) {
    this.refreshReconfigurators();
    return this.CH_RC.getReplicatedServers(name);
  }

  // refresh before returning
  public Set<NodeIDType> getReplicatedActives(String name) {
    this.refreshActives();
    return this.CH_AR.getReplicatedServers(name);
  }

  public ArrayList<InetAddress> getReplicatedActivesIPs(String name) {
    return this.getNodeIPs(this.getReplicatedActives(name));
  }

  public InterfaceReconfigurableNodeConfig<NodeIDType> getUnderlyingNodeConfig() {
    return this.nodeConfig;
  }

  // refresh consistent hash structure if changed
  private synchronized boolean refreshActives() {
    Set<NodeIDType> curActives = this.nodeConfig.getActiveReplicas();
    if (curActives.equals(this.getLastActives())) {
      return false;
    }
    this.setLastActives(curActives);
    this.CH_AR.refresh(curActives);
    return true;
  }

  // refresh consistent hash structure if changed
  private synchronized boolean refreshReconfigurators() {
    Set<NodeIDType> curReconfigurators = this.nodeConfig
            .getReconfigurators();
    if (curReconfigurators.equals(this.getLastReconfigurators())) {
      return false;
    }
    this.setLastReconfigurators(curReconfigurators);
    this.CH_RC.refresh(curReconfigurators);
    return true;
  }

  private synchronized Set<NodeIDType> getLastActives() {
    return this.activeReplicas;
  }

  private synchronized Set<NodeIDType> getLastReconfigurators() {
    return this.reconfigurators;
  }

  private synchronized Set<NodeIDType> setLastActives(
          Set<NodeIDType> curActives) {
    return this.activeReplicas = curActives;
  }

  private synchronized Set<NodeIDType> setLastReconfigurators(
          Set<NodeIDType> curReconfigurators) {
    return this.reconfigurators = curReconfigurators;
  }

  @Override
  public InetSocketAddress addReconfigurator(NodeIDType id,
          InetSocketAddress sockAddr) {
    InetSocketAddress isa = this.nodeConfig.addReconfigurator(id, sockAddr);
    return isa;
  }

  @Override
  public InetSocketAddress removeReconfigurator(NodeIDType id) {
    return this.nodeConfig.removeReconfigurator(id);
  }

  public InetSocketAddress slateForRemovalReconfigurator(NodeIDType id) {
    this.reconfiguratorsSlatedForRemoval.add(id);
    return this.getNodeSocketAddress(id);
  }

  @Override
  public InetSocketAddress addActiveReplica(NodeIDType id,
          InetSocketAddress sockAddr) {
    return this.nodeConfig.addActiveReplica(id, sockAddr);
  }

  @Override
  public InetSocketAddress removeActiveReplica(NodeIDType id) {
    return this.nodeConfig.removeActiveReplica(id);
  }

  @Override
  public long getVersion() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
