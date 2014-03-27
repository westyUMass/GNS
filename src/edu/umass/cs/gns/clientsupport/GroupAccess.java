package edu.umass.cs.gns.clientsupport;

import edu.umass.cs.gns.nameserver.ResultValue;
import edu.umass.cs.gns.packet.NSResponseCode;

//import edu.umass.cs.gns.packet.QueryResultValue;
/**
 * GroupAccess provides an interface to the group information in the GNS.
 *
 * The members of a group are stored in a record whose key is the GROUP string.
 *
 * @author westy
 */
public class GroupAccess {

  /**
   * Hidden field that stores group members
   */
  public static final String GROUP = InternalField.makeInternalFieldString("group");
  /**
   * Hidden field that stores what groups a GUID is a member of
   */
  public static final String GROUPS = InternalField.makeInternalFieldString("groups");
  /**
   * Hidden field that stores group member join requests
   */
  public static final String JOINREQUESTS = InternalField.makeInternalFieldString("groupJoinRequests");
  /**
   * Hidden field that stores group member quit requests
   */
  public static final String LEAVEREQUESTS = InternalField.makeInternalFieldString("groupLeaveRequests");

  /**
   * Sends a request to the NS to add a single GUID to a group.
   *
   * @param guid
   * @param memberGuid
   * @param writer
   * @param signature
   * @param message
   * @return
   */
  public static NSResponseCode addToGroup(String guid, String memberGuid, String writer, String signature, String message) {

    NSResponseCode groupResponse = Intercessor.sendUpdateRecord(guid, GROUP, memberGuid, null,
            UpdateOperation.APPEND_OR_CREATE, writer, signature, message);
    // We could roll back the above operation if the one below gets an error, but we don't
    // We'll worry about that when we migrate this into the Name Server
    if (!groupResponse.isAnError()) {
      Intercessor.sendUpdateRecordBypassingAuthentication(memberGuid, GROUPS, guid, null,
              UpdateOperation.APPEND_OR_CREATE);
    }
    return groupResponse;
  }

  /**
   * Sends a request to the NS to add a list of GUIDs to a group.
   *
   * @param guid
   * @param members
   * @param writer
   * @param signature
   * @param message
   * @return
   */
  public static NSResponseCode addToGroup(String guid, ResultValue members, String writer, String signature, String message) {
    NSResponseCode groupResponse = Intercessor.sendUpdateRecord(guid, GROUP, members, null,
            UpdateOperation.APPEND_OR_CREATE, writer, signature, message);
    if (!groupResponse.isAnError()) {
      // We could fix the above operation if any one below gets an error, but we don't
      // We'll worry about that when we migrate this into the Name Server
      for (String memberGuid : members.toStringSet()) {
        Intercessor.sendUpdateRecordBypassingAuthentication(memberGuid, GROUPS, guid, null,
                UpdateOperation.APPEND_OR_CREATE);
      }
    }
    return groupResponse;
  }

  /**
   * Sends a request to the NS to remove a single GUID from a group.
   *
   * @param guid
   * @param memberGuid
   * @param writer
   * @param signature
   * @param message
   * @return
   */
  public static NSResponseCode removeFromGroup(String guid, String memberGuid, String writer, String signature, String message) {
    NSResponseCode groupResponse = Intercessor.sendUpdateRecord(guid, GROUP, memberGuid, null, UpdateOperation.REMOVE, writer, signature, message);
    // We could roll back the above operation if the one below gets an error, but we don't
    // We'll worry about that when we migrate this into the Name Server
    if (!groupResponse.isAnError()) {
      Intercessor.sendUpdateRecordBypassingAuthentication(memberGuid, GROUPS, guid, null,
              UpdateOperation.REMOVE);
    }
    return groupResponse;
  }

  /**
   * Sends a request to the NS to remove a list of GUIDs from a group.
   *
   * @param guid
   * @param members
   * @param writer
   * @param signature
   * @param message
   * @return
   */
  public static NSResponseCode removeFromGroup(String guid, ResultValue members, String writer, String signature, String message) {
    NSResponseCode groupResponse = Intercessor.sendUpdateRecord(guid, GROUP, members, null,
            UpdateOperation.REMOVE, writer, signature, message);
    if (!groupResponse.isAnError()) {
      // We could fix the above operation if any one below gets an error, but we don't
      // We'll worry about that when we migrate this into the Name Server
      for (String memberGuid : members.toStringSet()) {
        Intercessor.sendUpdateRecordBypassingAuthentication(memberGuid, GROUPS, guid, null,
                UpdateOperation.REMOVE);
      }
    }
    return groupResponse;
  }

  /**
   * Returns the members of the group GUID.
   *
   * @param guid
   * @param reader
   * @param signature
   * @param message
   * @return
   */
  public static ResultValue lookup(String guid, String reader, String signature, String message) {
    QueryResult result = Intercessor.sendQuery(guid, GROUP, reader, signature, message);
    if (!result.isError()) {
      return new ResultValue(result.get(GROUP));
    } else {
      return new ResultValue();
    }
  }

  /**
   * Returns the groups that a GUID is a member of.
   *
   * @param guid
   * @param reader
   * @param signature
   * @param message
   * @return
   */
  public static ResultValue lookupGroups(String guid, String reader, String signature, String message) {
    QueryResult result = Intercessor.sendQuery(guid, GROUPS, reader, signature, message);
    if (!result.isError()) {
      return new ResultValue(result.get(GROUPS));
    } else {
      return new ResultValue();
    }
  }

  /**
   * Removes all group links when we're deleting a guid.
   * 
   * @param guid 
   */
  public static void cleanupGroupsForDelete(String guid) {
    // just so you know all the nulls mean we're ignoring signatures and authentication
    for (String groupGuid : GroupAccess.lookupGroups(guid, null, null, null).toStringSet()) {
      removeFromGroup(groupGuid, guid, null, null, null);
    }
  }

  public static NSResponseCode requestJoinGroup(String guid, String memberGuid, String writer, String signature, String message) {

    return Intercessor.sendUpdateRecord(guid, JOINREQUESTS, memberGuid, null, UpdateOperation.APPEND_OR_CREATE, writer, signature, message);
  }

  public static NSResponseCode requestLeaveGroup(String guid, String memberGuid, String writer, String signature, String message) {

    return Intercessor.sendUpdateRecord(guid, LEAVEREQUESTS, memberGuid, null, UpdateOperation.APPEND_OR_CREATE, writer, signature, message);
  }

  public static ResultValue retrieveGroupJoinRequests(String guid, String reader, String signature, String message) {
    QueryResult result = Intercessor.sendQuery(guid, JOINREQUESTS, reader, signature, message);
    if (!result.isError()) {
      return new ResultValue(result.get(JOINREQUESTS));
    } else {
      return new ResultValue();
    }
  }

  public static ResultValue retrieveGroupLeaveRequests(String guid, String reader, String signature, String message) {
    QueryResult result = Intercessor.sendQuery(guid, LEAVEREQUESTS, reader, signature, message);
    if (!result.isError()) {
      return new ResultValue(result.get(LEAVEREQUESTS));
    } else {
      return new ResultValue();
    }
  }

  public static boolean grantMembership(String guid, ResultValue requests, String writer, String signature, String message) {

    if (!addToGroup(guid, requests, writer, signature, message).isAnError()) {
      //if (!Intercessor.sendUpdateRecord(guid, GROUP, requests, null, UpdateOperation.APPEND_OR_CREATE, writer, signature, message).isAnError()) {
      if (!Intercessor.sendUpdateRecord(guid, JOINREQUESTS, requests, null, UpdateOperation.REMOVE, writer, signature, message).isAnError()) {
        return true;
      }
    }
    return false;
  }

  public static boolean revokeMembership(String guid, ResultValue requests, String writer, String signature, String message) {

    if (!removeFromGroup(guid, requests, writer, signature, message).isAnError()) {
      //if (!Intercessor.sendUpdateRecord(guid, GROUP, requests, null, UpdateOperation.REMOVE, writer, signature, message).isAnError()) {
      if (!Intercessor.sendUpdateRecord(guid, LEAVEREQUESTS, requests, null, UpdateOperation.REMOVE, writer, signature, message).isAnError()) {
        return true;
      }
    }
    return false;
  }
  public static String Version = "$Revision$";
}
