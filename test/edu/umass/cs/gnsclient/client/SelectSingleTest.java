/*
 *
 *  Copyright (c) 2015 University of Massachusetts
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you
 *  may not use this file except in compliance with the License. You
 *  may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  Initial developer(s): Westy
 *
 */
package edu.umass.cs.gnsclient.client;

import edu.umass.cs.gnsclient.client.oldclient.UniversalTcpClientExtended;
import edu.umass.cs.gnscommon.GnsProtocol;
import edu.umass.cs.gnsclient.client.util.GuidUtils;
import edu.umass.cs.gnsclient.client.util.JSONUtils;
import edu.umass.cs.gnsclient.client.util.ServerSelectDialog;
import edu.umass.cs.gnscommon.utils.RandomString;
import java.awt.geom.Point2D;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import static org.hamcrest.Matchers.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import static org.junit.Assert.*;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * Functionality test for core elements in the client using the UniversalGnsClientFull.
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SelectSingleTest {

  private static String accountAlias = "test@cgns.name"; // REPLACE THIS WITH YOUR ACCOUNT ALIAS
  private static String password = "password";
  private static UniversalTcpClientExtended client = null;
  /**
   * The address of the GNS server we will contact
   */
  private static InetSocketAddress address;
  private static GuidEntry masterGuid;
  private static GuidEntry westyEntry;
  private static GuidEntry samEntry;

  public SelectSingleTest() {
    if (client == null) {
      if (System.getProperty("host") != null
              && !System.getProperty("host").isEmpty()
              && System.getProperty("port") != null
              && !System.getProperty("port").isEmpty()) {
        address = new InetSocketAddress(System.getProperty("host"),
                Integer.parseInt(System.getProperty("port")));
      } else {
        address = ServerSelectDialog.selectServer();
      }
      System.out.println("Connecting to " + address.getHostName() + ":" + address.getPort());
      client = new UniversalTcpClientExtended(address.getHostName(), address.getPort(),
              System.getProperty("disableSSL").equals("true"));
      if (System.getProperty("alias") != null
              && !System.getProperty("alias").isEmpty()) {
        accountAlias = System.getProperty("alias");
      }
      if (System.getProperty("password") != null
              && !System.getProperty("password").isEmpty()) {
        password = System.getProperty("password");
      }
      try {
        masterGuid = GuidUtils.lookupOrCreateAccountGuid(client, accountAlias, password, true);
      } catch (Exception e) {
        fail("Exception while creating account guid: " + e);
      }
    }
  }

  @Test
  public void test_1_CreateGuids() {
    try {
      westyEntry = GuidUtils.registerGuidWithTestTag(client, masterGuid, "westy" + RandomString.randomString(6));
      samEntry = GuidUtils.registerGuidWithTestTag(client, masterGuid, "sam" + RandomString.randomString(6));
      System.out.println("Created: " + westyEntry);
      System.out.println("Created: " + samEntry);
    } catch (Exception e) {
      fail("Exception registering guids: " + e);
    }
  }

  @Test
  public void test_2_cats() {
    try {
      client.fieldCreateOneElementList(westyEntry.getGuid(), "cats", "whacky", westyEntry);

      assertEquals("whacky",
              client.fieldReadArrayFirstElement(westyEntry.getGuid(), "cats", westyEntry));

      client.fieldAppendWithSetSemantics(westyEntry.getGuid(), "cats", new JSONArray(
              Arrays.asList("hooch", "maya", "red", "sox", "toby")), westyEntry);

      HashSet<String> expected = new HashSet<String>(Arrays.asList("hooch",
              "maya", "red", "sox", "toby", "whacky"));
      HashSet<String> actual = JSONUtils.JSONArrayToHashSet(client
              .fieldReadArray(westyEntry.getGuid(), "cats", westyEntry));
      assertEquals(expected, actual);

      client.fieldClear(westyEntry.getGuid(), "cats", new JSONArray(
              Arrays.asList("maya", "toby")), westyEntry);
      expected = new HashSet<String>(Arrays.asList("hooch", "red", "sox",
              "whacky"));
      actual = JSONUtils.JSONArrayToHashSet(client.fieldReadArray(
              westyEntry.getGuid(), "cats", westyEntry));
      assertEquals(expected, actual);

      client.fieldReplaceFirstElement(westyEntry.getGuid(), "cats", "maya", westyEntry);
      assertEquals("maya",
              client.fieldReadArrayFirstElement(westyEntry.getGuid(), "cats", westyEntry));

      client.fieldAppendWithSetSemantics(westyEntry.getGuid(), "cats", "fred", westyEntry);
      expected = new HashSet<String>(Arrays.asList("maya", "fred"));
      actual = JSONUtils.JSONArrayToHashSet(client.fieldReadArray(
              westyEntry.getGuid(), "cats", westyEntry));
      assertEquals(expected, actual);

      client.fieldAppendWithSetSemantics(westyEntry.getGuid(), "cats", "fred", westyEntry);
      expected = new HashSet<String>(Arrays.asList("maya", "fred"));
      actual = JSONUtils.JSONArrayToHashSet(client.fieldReadArray(
              westyEntry.getGuid(), "cats", westyEntry));
      assertEquals(expected, actual);
    } catch (Exception e) {
      fail("Exception when we were not expecting testing DB: " + e);
    }
  }
  
  @Test
  public void test_3_BasicSelect() {
    try {
      JSONArray result = client.select("cats", "fred");
      // best we can do since there will be one, but possibly more objects in results
      assertThat(result.length(), greaterThanOrEqualTo(1));
    } catch (Exception e) {
      fail("Exception when we were not expecting it: " + e);
    }
  }

  @Test
  public void test_999_Stop() {
    try {
      client.stop();
    } catch (Exception e) {
      fail("Exception during stop: " + e);
    }
  }
}
