package eu.cloudtm.controller.oracles.common;   /*
 * INESC-ID, Instituto de Engenharia de Sistemas e Computadores Investigação e Desevolvimento em Lisboa
 * Copyright 2013 INESC-ID and/or its affiliates and other
 * contributors as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

/**
 * @author Diego Didona, didona@gsd.inesc-id.pt
 *         Date: 01/02/13
 */


import Tas2.core.environment.DSTMScenarioTas2;
import Tas2.core.environment.WorkParams;
import Tas2.exception.Tas2Exception;
import Tas2.physicalModel.cpunet.cpu.CpuServiceTimes;
import Tas2.physicalModel.cpunet.cpu.two.CpuServiceTimes2Impl;
import Tas2.physicalModel.cpunet.net.queue.NetServiceTimes;
import Tas2.physicalModel.cpunet.net.tas.FixedRttServiceTimes;
import eu.cloudtm.StatsManager;
import eu.cloudtm.common.dto.WhatIfCustomParamDTO;
import eu.cloudtm.controller.model.ACF;
import eu.cloudtm.wpm.logService.remote.events.PublishAttribute;
import eu.cloudtm.wpm.logService.remote.events.PublishMeasurement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class DSTMScenarioFactory {

    private static final Log log = LogFactory.getLog(DSTMScenarioFactory.class);


    private static final double CLOSED_SYSTEM = 0D;
    private static final boolean ROA = true;

    private static CpuServiceTimes2Impl cpu;
    private static WorkParams workParams;
    private static NetServiceTimes net;

    private DSTMScenarioFactory() {
    }

    public static DSTMScenarioTas2 buildScenario(Set<HashMap<String, PublishAttribute>> jmx,
                                                 Set<HashMap<String, PublishAttribute>> mem,
                                                 int nodes,
                                                 int threads,
                                                 double timeWindow)
            throws PublishAttributeException, Tas2Exception {

        buildBaseScenario(jmx, mem, nodes, threads, timeWindow);

        log.info("Most important features: wrPerc = " + workParams.getWritePercentage() + ", " +
                "wrPerTx " + workParams.getWriteOpsPerTx() +
                " updateTxS " + cpu.getUpdateTxLocalExecutionS() +
                " readOnlyTxS " + cpu.getReadOnlyTxLocalExecutionS() +
                " acf " + workParams.getApplicationContentionFactor());


        return new DSTMScenarioTas2(cpu, net, workParams);
    }

    public static DSTMScenarioTas2 buildCustomScenario(Set<HashMap<String, PublishAttribute>> jmx,
                                                Set<HashMap<String, PublishAttribute>> mem,
                                                WhatIfCustomParamDTO customParam,
                                                int nodes,
                                                int threads,
                                                double timeWindow)
            throws PublishAttributeException, Tas2Exception {

        buildBaseScenario(jmx, mem, nodes, threads, timeWindow);

        // customizing cpu params
        if (customParam.getLocalReadOnlyTxLocalServiceTime() != -1)
            cpu.setReadOnlyTxLocalExecutionS(StatsManager.getAvgAttribute("LocalReadOnlyTxLocalServiceTime", jmx));
        if (customParam.getLocalUpdateTxLocalServiceTime() != -1)
            cpu.setUpdateTxLocalExecutionS(StatsManager.getAvgAttribute("LocalUpdateTxLocalServiceTime", jmx));

        log.trace(cpu);

        // customizing work params
        if (customParam.getSuxNumPuts() != -1)
            workParams.setWriteOpsPerTx(customParam.getSuxNumPuts());
        if (customParam.getRetryWritePercentage() != -1)
            workParams.setWritePercentage(customParam.getRetryWritePercentage());
        if (customParam.getPrepareCommandBytes() != -1)
            workParams.setPrepareMessageSize(customParam.getPrepareCommandBytes());

        log.trace(workParams);

        // customizing net params
        log.trace(net);

        log.info("Most important features: wrPerc = " + workParams.getWritePercentage() + ", " +
                "wrPerTx " + workParams.getWriteOpsPerTx() +
                " updateTxS " + cpu.getUpdateTxLocalExecutionS() +
                " readOnlyTxS " + cpu.getReadOnlyTxLocalExecutionS() +
                " acf " + workParams.getApplicationContentionFactor());

        return new DSTMScenarioTas2(cpu, net, workParams);
    }


    private static void buildBaseScenario(Set<HashMap<String, PublishAttribute>> jmx,
                                          Set<HashMap<String, PublishAttribute>> mem,
                                          int nodes,
                                          int threads,
                                          double timeWindow) throws PublishAttributeException {
        cpu = (CpuServiceTimes2Impl) buildCpuServiceTimes(jmx);
        log.trace(cpu);

        workParams = buildWorkloadParams(jmx, mem, nodes, threads, timeWindow);
        log.trace(workParams);

        net = buildNetServiceTimes(jmx);
        log.trace(net);
        return;
    }

    private Set<HashMap<String, PublishAttribute>> toHashMapSet(Set<PublishMeasurement> measurements) {
        Set<HashMap<String, PublishAttribute>> set = new HashSet<HashMap<String, PublishAttribute>>();
        for (PublishMeasurement m : measurements) {
            set.add(m.getValues());
        }
        return set;
    }

    private static CpuServiceTimes buildCpuServiceTimes(Set<HashMap<String, PublishAttribute>> values) throws PublishAttributeException {
        //Local Update
        double updateLocalTxLocalExec = StatsManager.getAvgAttribute("LocalUpdateTxLocalServiceTime", values);
        double updateLocalTxPrepare = StatsManager.getAvgAttribute("LocalUpdateTxPrepareServiceTime", values);
        double updateLocalTxCommit = StatsManager.getAvgAttribute("LocalUpdateTxCommitServiceTime", values);
        double updateLocalTxLocalRollback = StatsManager.getAvgAttribute("LocalUpdateTxLocalRollbackResponseTime", values);
        double updateLocalTxRemoteRollback = StatsManager.getAvgAttribute("LocalUpdateTxRemoteRollbackServiceTime", values);
        //Local Read Only
        //TODO this should be the service time. I only have the response time due to a bug. It's +/- the same if I don't vary the threads and keep the load low

        // FABIO FABIO FABIO ---> questa è vecchia da sostituire con LocalReadOnlyTxLocalServiceTime
        //double readOnlyTxLocalExec = getAvgAttribute("AvgLocalReadOnlyExecutionTime",values);//getAvgAttribute("LocalReadOnlyTxLocalResponseTime", values);
        // FABIO FABIO FABIO
        double readOnlyTxLocalExec = StatsManager.getAvgAttribute("LocalReadOnlyTxLocalServiceTime", values);
        double readOnlyTxPrepare = StatsManager.getAvgAttribute("LocalReadOnlyTxPrepareServiceTime", values);//("ReadOnlyCommitCpuTime");
        double readOnlyTxCommit = StatsManager.getAvgAttribute("LocalReadOnlyTxCommitServiceTime", values);
        //Remote Update
        double updateRemoteTxLocalExec = StatsManager.getAvgAttribute("RemoteUpdateTxPrepareServiceTime", values);
        double updateRemoteTxCommit = StatsManager.getAvgAttribute("RemoteUpdateTxCommitServiceTime", values);
        double updateRemoteTxRollback = StatsManager.getAvgAttribute("RemoteUpdateTxRollbackServiceTime", values);

        CpuServiceTimes2Impl cpu = new CpuServiceTimes2Impl();
        cpu.setUpdateTxLocalExecutionS(updateLocalTxLocalExec);
        cpu.setUpdateTxPrepareS(updateLocalTxPrepare);
        cpu.setUpdateTxCommitS(updateLocalTxCommit);
        cpu.setUpdateTxLocalLocalRollbackS(updateLocalTxLocalRollback);
        cpu.setUpdateTxLocalRemoteRollbackS(updateLocalTxRemoteRollback);

        cpu.setReadOnlyTxLocalExecutionS(readOnlyTxLocalExec);
        cpu.setReadOnlyTxPrepareS(readOnlyTxPrepare);
        cpu.setReadOnlyTxCommitS(readOnlyTxCommit);

        cpu.setUpdateTxRemoteExecutionS(updateRemoteTxLocalExec);
        cpu.setUpdateTxRemoteCommitS(updateRemoteTxCommit);
        cpu.setUpdateTxRemoteRollbackS(updateRemoteTxRollback);


        return cpu;
    }

    private static WorkParams buildWorkloadParams(Set<HashMap<String, PublishAttribute>> JMXvalues,
                                                  Set<HashMap<String, PublishAttribute>> MEMvalues,
                                                  int nodes,
                                                  int threads,
                                                  double timeWindow)
            throws PublishAttributeException {

        //TODO: check
        double wrOps = (double) ((int) StatsManager.getAvgAttribute("SuxNumPuts", JMXvalues));
        boolean RoA = ROA;
        double wrPer = StatsManager.getAvgAttribute("RetryWritePercentage", JMXvalues);
        double lambda = CLOSED_SYSTEM;
        double mexSize = StatsManager.getAvgAttribute("PrepareCommandBytes", JMXvalues);


        double acf = ACF.evaluate(JMXvalues, threads, timeWindow);//rrp.getAcfFromInversePrepareProb(numThreads,wrOps);//rrp.getClosedAcf(numThreads);

        double mem = StatsManager.getAvgAttribute("MemoryInfo.used", MEMvalues);

        WorkParams workParams = new WorkParams();
        workParams.setRetryOnAbort(RoA);
        workParams.setWriteOpsPerTx(wrOps);
        workParams.setWritePercentage(wrPer);
        workParams.setLambda(lambda);
        workParams.setPrepareMessageSize(mexSize);
        workParams.setNumNodes(nodes);
        workParams.setThreadsPerNode(threads);
        workParams.setApplicationContentionFactor(acf);
        workParams.setMem(mem);

        return workParams;
    }

    private static NetServiceTimes buildNetServiceTimes(Set<HashMap<String, PublishAttribute>> JMXvalues) {
        return new FixedRttServiceTimes(1, 1);
    }
}