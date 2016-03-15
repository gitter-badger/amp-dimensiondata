/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.dimensiondata.cloudcontroller.compute;

import static org.testng.Assert.assertEquals;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;
import org.jclouds.ssh.SshClient;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "DimensionDataCloudControllerComputeServiceContextLiveTest")
public class DimensionDataCloudControllerComputeServiceContextLiveTest extends BaseComputeServiceContextLiveTest {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   public DimensionDataCloudControllerComputeServiceContextLiveTest() {
      provider = "dimensiondata-cloudcontroller";
   }

   @Test
   public void testListHardwareProfiles() {
      for (Hardware hardware : view.getComputeService().listHardwareProfiles()) {
         System.out.println(hardware);
      }
   }

   @Test
   public void testListAvailableLocations() throws RunNodesException {
      for (Location location : view.getComputeService().listAssignableLocations()) {
         System.out.println(location);
      }
   }

   @Test
   public void testListImages() throws RunNodesException {
      for (Image image : view.getComputeService().listImages()) {
         System.out.println(image);
      }
   }

   @Test
   public void testListNodes() throws RunNodesException {
      for (ComputeMetadata node : view.getComputeService().listNodes()) {
         System.out.println(node);
      }
   }

   @Test
   public void testLaunchClusterWithDomainName() throws RunNodesException {
      int numNodes = 1;
      final String name = "node";
      Template template = view.getComputeService().templateBuilder().build();

      Set<? extends NodeMetadata> nodes = view.getComputeService().createNodesInGroup(name, numNodes, template);
      assertEquals(numNodes, nodes.size(), "wrong number of nodes");
      for (NodeMetadata node : nodes) {
         logger.debug("Created Node: %s", node);
         SshClient client = view.utils().sshForNode().apply(node);
         client.connect();
         ExecResponse hello = client.exec("mount");
         logger.debug(hello.getOutput().trim());
         view.getComputeService().destroyNode(node.getId());
      }
   }

}