/*
 * Copyright (C) 2014
 * University of Massachusetts
 * All Rights Reserved 
 *
 * Initial developer(s): Westy.
 */
package edu.umass.cs.gns.commands.admin;

import edu.umass.cs.gns.clientsupport.CommandResponse;
import static edu.umass.cs.gns.clientsupport.Defs.*;
import edu.umass.cs.gns.commands.CommandModule;
import edu.umass.cs.gns.commands.GnsCommand;
import edu.umass.cs.gns.clientCommandProcessor.ClientRequestHandlerInterface;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author westy
 */
public class GetTagged extends GnsCommand {

  public GetTagged(CommandModule module) {
    super(module);
  }

  @Override
  public String[] getCommandParameters() {
    return new String[]{NAME};
  }

  @Override
  public String getCommandName() {
    return GETTAGGED;
  }

  @Override
  @SuppressWarnings("unchecked")
  public CommandResponse execute(JSONObject json, ClientRequestHandlerInterface handler) throws InvalidKeyException, InvalidKeySpecException,
          JSONException, NoSuchAlgorithmException, SignatureException {
    String tagName = json.getString(NAME);
    return new CommandResponse(new JSONArray(handler.getAdmintercessor().collectTaggedGuids(tagName, handler)).toString());
  }

  @Override
  public String getCommandDescription() {
    return "Returns all guids that contain the tag.";
  }
}
