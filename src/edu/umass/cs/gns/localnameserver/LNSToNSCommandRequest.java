/*
 * Copyright (C) 2013
 * University of Massachusetts
 * All Rights Reserved 
 */
package edu.umass.cs.gns.localnameserver;

import edu.umass.cs.gns.clientsupport.Defs;
import edu.umass.cs.gns.main.GNS;
import edu.umass.cs.gns.nsdesign.packet.LNSToNSCommandPacket;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.UnknownHostException;

/**
 * Handles sending and receiving of commands.
 *
 * @author westy
 */
public class LNSToNSCommandRequest {

  public static void handlePacketCommandRequest(JSONObject incomingJSON, ClientRequestHandlerInterface handler) throws JSONException, UnknownHostException {

    LNSToNSCommandPacket packet = new LNSToNSCommandPacket(incomingJSON);
    if (packet.getReturnValue() == null) {
      // PACKET IS GOING OUT TO A NAME SERVER
      // If the command is a guid or name command (has a GUID or NAME field) we MUST send it to a server 
      // that is the active server for that GUID
      // because the code at the name server assumes it can look up the info for that record locally on the server.
      // We pick a name server based on that record name using the active name servers info in the cache.
      Object serverID = pickNameServer(getUsefulRecordName(packet.getCommand()), handler);
      GNS.getLogger().info("LNS" + handler.getNodeAddress() + " transmitting CommandPacket " + incomingJSON + " to " + serverID);
      handler.sendToNS(incomingJSON, serverID);
    } else {
      // PACKET IS COMING BACK FROM A NAMESERVER
      handler.getIntercessor().handleIncomingPacket(incomingJSON);
    }
  }

  /**
   * Look up the name or guid in the command
   *
   * @param commandJSON
   * @return
   */
  private static String getUsefulRecordName(JSONObject commandJSON) {
    // try a couple
    try {
      return commandJSON.getString(Defs.GUID);
    } catch (JSONException e) {
      try {
        return commandJSON.getString(Defs.NAME);
      } catch (JSONException f) {
        return null;
      }
    }
  }

  /**
   * Picks a name server based on the guid.
   *
   * @param guid
   * @return
   */
  private static Object pickNameServer(String guid, ClientRequestHandlerInterface handler) {
    if (guid != null) {
      CacheEntry cacheEntry = handler.getCacheEntry(guid);
      // PRoBABLY WILL NEED SOMETHING IN HERE TO FORCE IT TO UPDATE THE ActiveNameServers
      if (cacheEntry != null && cacheEntry.getActiveNameServers() != null && !cacheEntry.getActiveNameServers().isEmpty()) {
        Object id = handler.getGnsNodeConfig().getClosestServer(cacheEntry.getActiveNameServers());
        if (id != null) {
          GNS.getLogger().info("@@@@@@@ Picked NS" + id + " for record " + guid);
          return id;
        }
      }
      GNS.getLogger().warning("!?!?!?!?!?!?!?! NO SERVER FOR NS for record " + guid);
    }
    return handler.getGnsNodeConfig().getClosestServer(handler.getGnsNodeConfig().getNodeIDs());
  }
}
