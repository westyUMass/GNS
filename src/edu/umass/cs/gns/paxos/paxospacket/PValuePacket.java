package edu.umass.cs.gns.paxos.paxospacket;

import org.json.JSONException;
import org.json.JSONObject;

import edu.umass.cs.gns.paxos.Ballot;

import edu.umass.cs.gns.util.Stringifiable;
import java.io.Serializable;

@Deprecated
public class PValuePacket<NodeIDType> extends PaxosPacket implements Serializable {

  public Ballot<NodeIDType> ballot;
  public ProposalPacket<NodeIDType> proposal;

  public PValuePacket(Ballot<NodeIDType> b, ProposalPacket<NodeIDType> p) {
    this.ballot = b;
    this.proposal = p;
  }

  static String BALLOT = "b1";

  public PValuePacket(JSONObject json, Stringifiable<NodeIDType> unstringer) throws JSONException {
    this.proposal = new ProposalPacket<NodeIDType>(json, unstringer);
    this.ballot = new Ballot<NodeIDType>(json.getString(BALLOT));
  }

  @Override
  public JSONObject toJSONObject() throws JSONException {
    JSONObject json = this.proposal.toJSONObject();
    json.put(BALLOT, ballot.toString());
    json.put(PaxosPacket.PACKET_TYPE_FIELD_NAME, packetType);
    return json;
  }

}
