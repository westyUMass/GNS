/*
 * Copyright (C) 2014
 * University of Massachusetts
 * All Rights Reserved 
 */
package edu.umass.cs.gns.clientprotocol;

/**
 *
 * @author westy
 */
public class Defs {
  
  
  public final static String REGISTERACCOUNT = "registerAccount";
  public final static String VERIFYACCOUNT = "verifyAccount";
  public final static String ADDALIAS = "addAlias";
  public final static String REMOVEALIAS = "removeAlias";
  public final static String RETRIEVEALIASES = "retrieveAliases";
  public final static String ADDGUID = "addGuid";
  public final static String REMOVEGUID = "removeGuid";
  public final static String SETPASSWORD = "setPassword";
  public final static String REMOVEACCOUNT = "removeAccount";
  public final static String LOOKUPGUID = "lookupGuid";
  public final static String LOOKUPGUIDRECORD = "lookupGuidRecord";
  public final static String LOOKUPACCOUNTRECORD = "lookupAccountRecord";
  //
  // new
  public final static String CREATE = "create";
  public final static String APPENDORCREATE = "appendOrCreate";
  public final static String REPLACE = "replace";
  public final static String REPLACEORCREATE = "replaceOrCreate";
  public final static String APPENDWITHDUPLICATION = "appendDup";
  public final static String APPEND = "append";
  public final static String REMOVE = "remove";
  public final static String CREATELIST = "createList";
  public final static String APPENDORCREATELIST = "appendOrCreateList";
  public final static String REPLACEORCREATELIST = "replaceOrCreateList";
  public final static String REPLACELIST = "replaceList";
  public final static String APPENDLISTWITHDUPLICATION = "appendListDup";
  public final static String APPENDLIST = "appendList";
  public final static String REMOVELIST = "removeList";
  public final static String SUBSTITUTE = "substitute";
  public final static String SUBSTITUTELIST = "substituteList";
  public final static String CLEAR = "clear";
  public final static String READ = "read";
  public final static String READONE = "readOne";
  public final static String SELECT = "select";
  public final static String WITHIN = "within";
  public final static String NEAR = "near";
  public final static String QUERY = "query";
  public final static String MAXDISTANCE = "maxDistance";
  public final static String REMOVEFIELD = "removeField";
  //
  public final static String ACLADD = "aclAdd";
  public final static String ACLREMOVE = "aclRemove";
  public final static String ACLRETRIEVE = "aclRetrieve";
  public final static String ADDTOGROUP = "addToGroup";
  public final static String REMOVEFROMGROUP = "removeFromGroup";
  public final static String GETGROUPMEMBERS = "getGroupMembers";
  //
  public final static String REQUESTJOINGROUP = "requestJoinGroup";
  public final static String RETRIEVEGROUPJOINREQUESTS = "retrieveGroupJoinRequests";
  public final static String GRANTMEMBERSHIP = "grantMembership";
  public final static String REQUESTLEAVEGROUP = "requestLeaveGroup";
  public final static String RETRIEVEGROUPLEAVEREQUESTS = "retrieveGroupLeaveRequests";
  public final static String REVOKEMEMBERSHIP = "revokeMembership";
  //
  public final static String HELP = "help";
  // admin commands 
  public final static String ADMIN = "admin";
  public final static String DELETEALLRECORDS = "deleteAllRecords";
  public final static String RESETDATABASE = "resetDatabase";
  public final static String CLEARCACHE = "clearCache";
  public final static String DUMPCACHE = "dumpCache";
  public final static String SETPARAMETER = "setParameter";
  public final static String GETPARAMETER = "getParameter";
  //public final static String DELETEALLGUIDRECORDS = "deleteAllGuidRecords";
  public final static String DUMP = "dump";
  public final static String ADDTAG = "addTag";
  public final static String REMOVETAG = "removeTag";
  public final static String GETTAGGED = "getTagged";
  public final static String CLEARTAGGED = "clearTagged";
  //
  public final static String OKRESPONSE = "+OK+";
  public final static String NULLRESPONSE = "+EMPTY+";
  public final static String BADRESPONSE = "+NO+";
  public final static String BADSIGNATURE = "+BADSIGNATURE+";
  public final static String ACCESSDENIED = "+ACCESSDENIED+";
  public final static String OPERATIONNOTSUPPORTED = "+OPERATIONNOTSUPPORTED+";
  public final static String QUERYPROCESSINGERROR = "+QUERYPROCESSINGERROR+";
  public final static String NOACTIONFOUND = "+NOACTIONFOUND+";
  public final static String BADREADERGUID = "+BADREADERGUID+";
  public final static String BADWRITERGUID = "+BADWRITERGUID+";
  public final static String BADGUID = "+BADGUID+";
  public final static String BADALIAS = "+BADALIAS+";
  public final static String BADACCOUNT = "+BADACCOUNT+";
  public final static String BADGROUP = "+BADGROUP+";
  public final static String BADFIELD = "+BADFIELD+";
  public final static String BADACLTYPE = "+BADACLTYPE+";
  public final static String DUPLICATENAME = "+DUPLICATENAME+";
  public final static String DUPLICATEGUID = "+DUPLICATEGUID+";
  public final static String DUPLICATEGROUP = "+DUPLICATEGROUP+";
  public final static String DUPLICATEFIELD = "+DUPLICATEFIELD+";
  public final static String JSONPARSEERROR = "+JSONPARSEERROR+";
  public final static String VERIFICATIONERROR = "+VERIFICATIONERROR+";
  public final static String TOMANYALIASES = "+TOMANYALIASES+";
  public final static String TOMANYGUIDS = "+TOMANYGUIDS+";
  public final static String UPDATEERROR = "+UPDATEERROR+";
  public final static String SELECTERROR = "+SELECTERROR+";
  public final static String GENERICEERROR = "+GENERICEERROR+";
  public final static String ALLFIELDS = "+ALL+";
  public final static String EVERYONE = "+ALL+";
  public final static String EMPTY = "+EMPTY+";
  //
  public static final String RASALGORITHM = "RSA";
  public static final String SIGNATUREALGORITHM = "SHA1withRSA";
  public final static String NEWLINE = System.getProperty("line.separator");
  // Fields for HTTP get queries
  public final static String NAME = "name";
  public final static String GUID = "guid";
  public final static String GUID2 = "guid2";
  public final static String READER = "reader";
  public final static String WRITER = "writer";
  public final static String APPGUID = "appGuid";
  public final static String ACCESSER = "accesser";
  public final static String FIELD = "field";
  public final static String VALUE = "value";
  public final static String OLDVALUE = "oldvalue";
  public final static String MEMBER = "member";
  public final static String MEMBERS = "members";
  // Fields for HTTP get queries
  public final static String ACLTYPE = "aclType";
  // Special fields for ACL 
  public final static String GUID_ACL = "+GUID_ACL+";
  public final static String GROUP_ACL = "+GROUP_ACL+";
  //public final static String JSONSTRING = "jsonstring";
  public final static String GROUP = "group";
  public final static String PUBLICKEY = "publickey";
  public final static String PASSWORD = "password";
  public final static String CODE = "code";
  public final static String SIGNATURE = "signature";
  public final static String PASSKEY = "passkey";
  //public final static String TABLE = "table";
  // Blessed field names
  public static final String LOCATION_FIELD_NAME = "location";
  public static final String IPADDRESS_FIELD_NAME = "ipAddress";
  
  
  
}