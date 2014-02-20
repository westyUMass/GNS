package edu.umass.cs.gns.paxos;

//import edu.umass.cs.gns.nameserver.NameServer;
import edu.umass.cs.gns.nio.GNSNIOTransport;
import edu.umass.cs.gns.nio.NioServer;
import edu.umass.cs.gns.packet.paxospacket.FailureDetectionPacket;
import edu.umass.cs.gns.packet.paxospacket.RequestPacket;
import org.json.JSONException;

import java.io.IOException;

/**
 * We use this paxos interface object while running tests for paxos module.
 *
 * The main task of this module is to send response to client node after a request is executed.
 *
 * NOTE: We have hardcoded that nodeID = 0 will respond to client. Therefore, in our tests
 * nodeID = 0 must be a paxos replica and it must not be crashed.
 *
 * User: abhigyan
 * Date: 6/29/13
 * Time: 8:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultPaxosInterface implements PaxosInterface {
  /**
   *
   */
  int nodeID;

  /**
   * Transport object. It is needed to send responses to client.
   */
  NioServer nioServer;


  public DefaultPaxosInterface(int nodeID, NioServer nioServer) {
    this.nodeID = nodeID;
    this.nioServer = nioServer;
  }

  @Override
  public void handlePaxosDecision(String paxosID, RequestPacket requestPacket, boolean recovery) {
    // check
    if (nodeID == 0)
      try {
        nioServer.sendToID(requestPacket.clientID, requestPacket.toJSONObject());
      } catch (JSONException e) {
        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      } catch (IOException e) {
        e.printStackTrace();
      }
  }

  @Override
  public void handleFailureMessage(FailureDetectionPacket fdPacket) {
    //
  }

  @Override
  public String getState(String paxosID) {
    return "ABCD\nEFGH\nIJKL\nMNOP\n";
  }

  @Override
  public void updateState(String paxosID, String state) {
    // empty method becasue this only for running paxos tests independently
  }

  @Override
  public String getPaxosKeyForPaxosID(String paxosID) {
    return paxosID;
  }
}
