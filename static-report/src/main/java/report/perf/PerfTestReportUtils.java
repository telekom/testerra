/*
 * Testerra
 *
 * (C) 2020,  Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
 package report.perf;

import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;
import eu.tsystems.mms.tic.testframework.internal.TimingInfo;
import eu.tsystems.mms.tic.testframework.utils.NumberUtils;
import org.testng.ITestResult;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;


public final class PerfTestReportUtils {

    private PerfTestReportUtils() {
    }

    /**
     * contains all pageLoadInfos of one specific test step action: String = action name
     */
    private static Map<String, List<TimingInfo>> pageLoadInfosPerTestStepAction = new HashMap<String, List<TimingInfo>>();

    /**
     * contains all pageLoadInfos of all testruns of a specific VU for all VUs
     */
    private static Map<String, List<List<TimingInfo>>> allPageLoadInfosPerVU = new HashMap<String, List<List<TimingInfo>>>();

    /**
     * contains all pageLoadInfos of one specific test method: all repetitions of all virtual users
     */
    private static Map<Long, List<TimingInfo>> pageLoadInfosPerTestMethodInvocation = new HashMap<Long, List<TimingInfo>>();

    /**
     * contains averageResponseTime of every test repetition
     */
    private static Map<Integer, Long> averageResponseTimePerTestRepetition = new HashMap<Integer, Long>();

    /**
     * contains the max response time of every test repetition
     */
    private static Map<Integer, Long> maxResponseTimePerTestRepetition = new HashMap<Integer, Long>();

    /**
     * contaims the min response time of every test repetition
     */
    private static Map<Integer, Long> minResponseTimePerTestRepetition = new HashMap<Integer, Long>();

    /**
     * contains averageResponseTime of every test step
     */
    private static Map<Integer, Long> averageResponseTimePerTestStep = new HashMap<Integer, Long>();

    /**
     * contains the max response time of every test step
     */
    private static Map<Integer, Long> maxResponseTimePerTestStep = new HashMap<Integer, Long>();

    /**
     * contains min response time of every test step
     */
    private static Map<Integer, Long> minResponseTimePerTestStep = new HashMap<Integer, Long>();

    /**
     * contains averageResponseTime of every test step
     */
    private static Map<String, Long> averageResponseTimePerTestStepAction = new HashMap<String, Long>();

    /**
     * contains the max response time of every test step
     */
    private static Map<String, Long> maxResponseTimePerTestStepAction = new HashMap<String, Long>();

    /**
     * contains min response time of every test step
     */
    private static Map<String, Long> minResponseTimePerTestStepAction = new HashMap<String, Long>();

    /**
     * contains all Response time per Teststep
     */
    private static Map<Integer, List<TimingInfo>> allResponseTimesPerTestStep = new HashMap<Integer, List<TimingInfo>>();

    /**
     * contains the amount of transaction status of the whole test (all reruns (transaction))
     */
    private static Map<String, Integer> transactionStatus = new TreeMap<String, Integer>();

    /**
     * contains amount of Transactions per second
     */
    private static Map<Long, Integer> transactionsPerSecond = new HashMap<Long, Integer>();

    /**
     * contains amount of Transactions per minute
     */
    private static Map<Long, Integer> transactionsPerMinute = new HashMap<Long, Integer>();

    /**
     * contains amount of Transactions per hour
     */
    private static Map<Long, Integer> transactionsPerHour = new HashMap<Long, Integer>();

    /**
     * contains amount of requests per second
     */
    private static Map<Long, Integer> requestsPerSecond = new HashMap<Long, Integer>();

    /**
     * contains amount requests per minute
     */
    private static Map<Long, Integer> requestsPerMinute = new HashMap<Long, Integer>();

    /**
     * contains amount requests per hour
     */
    private static Map<Long, Integer> requestsPerHour = new HashMap<Long, Integer>();

    /**
     * String to mark endTime of a Test in List<PageLoadInfos>
     */
    public static final String TEST_END = "Test_End";

    /**
     * String Succes for transactionStatus Map
     */
    public static final String TEXT_TEST_SUCCESS = "Success";

    /**
     * String Fail for transactionStatus Map
     */
    public static final String TEXT_TEST_FAIL = "Fail";

    /**
     * String Skip for transactionStatus Map
     */
    public static final String TEXT_TEST_SKIP = "Skip";

    /**
     * folder name for saving CSVs, Hars and Graphs
     */
    private static String destinationFolder = "unknown";

    /**
     * subFolderName for CSV
     */
    private static final String SUB_FOLDER_OF_CSV = "csv_per_vu\\";

    /**
     * subFolderName for testconfig
     */
    private static final String SUB_FOLDER_TEST_CONFIG = "testconfig\\";

    /**
     * number of test steps of a test method
     */
    private static int numberOfTestSteps = 0;

    /**
     * method to initialize data for graph generation
     * @param pageLoadInfos
     */
    public static void initializeDataForGraphGeneration(Map<Long, List<TimingInfo>> pageLoadInfos){
        initializeAllPageLoadInfosPerVU(pageLoadInfos);
        generateNumberOfTestSteps();
    }

    /**
     * initializes the Map of PageLoadInfos per VU from the raw data of LoadTestContainer
     *
     * @param pageLoadInfos
     * @param pageLoadInfos .
     */
     private static void initializeAllPageLoadInfosPerVU(Map<Long, List<TimingInfo>> pageLoadInfos) {
        Set<Long> idSet = pageLoadInfos.keySet();
        pageLoadInfosPerTestMethodInvocation = pageLoadInfos;

        // clear the List before filling it due to Iteration of of all TestMethods in GraphGenerator.
        allPageLoadInfosPerVU.clear();
        for (Long threadId : idSet) {
            String threadName = PerfTestContainer.getThreadNameByID(threadId);
            ITestResult iTestResult = PerfTestContainer.getTestResult(threadId);

            List<TimingInfo> timingInfosToUpdate = new ArrayList<TimingInfo>();
            List<List<TimingInfo>> pageLoadInfosPerRepetition = null;
            if (allPageLoadInfosPerVU.get(threadName) == null) {
                pageLoadInfosPerRepetition = new ArrayList<List<TimingInfo>>();
            } else {
                pageLoadInfosPerRepetition = allPageLoadInfosPerVU.get(threadName);
            }

            List<TimingInfo> timingInfoOfTestRunOfUser = pageLoadInfos.get(threadId);
            // add PageLoadInfos to the existing  List of a VU
            for (TimingInfo timingInfoOfTest : timingInfoOfTestRunOfUser) {
                timingInfosToUpdate.add(timingInfoOfTest);
            }
            //create a dummy TimingInfo which indicates the endTime of one run for SUCCESSFUL tests
            if (iTestResult.getStatus() == ITestResult.SUCCESS) {
                TimingInfo testEnd = new TimingInfo(null, "no url", 0L, iTestResult.getEndMillis());
                timingInfosToUpdate.add(testEnd);
            }
            pageLoadInfosPerRepetition.add(timingInfosToUpdate);
            allPageLoadInfosPerVU.put(threadName, pageLoadInfosPerRepetition);
        }
    }

    /**
     * set the numberOfTestStep of a test method
     */
    private static void generateNumberOfTestSteps() {
        int maxNumberOfTestStepsOfTestMethod = 0;
        Set<Long> threadIDs = pageLoadInfosPerTestMethodInvocation.keySet();
        for (long threadID : threadIDs) {
            int testStepsOfTestMethodRepetition = pageLoadInfosPerTestMethodInvocation.get(threadID).size();
            // overwrite numberOfSteps only in case of higher value of the list size of the current TestStep
            if (testStepsOfTestMethodRepetition > maxNumberOfTestStepsOfTestMethod) {
                maxNumberOfTestStepsOfTestMethod = testStepsOfTestMethodRepetition;
            }
        }
        numberOfTestSteps = maxNumberOfTestStepsOfTestMethod;
    }

    /**
     * generates min, avg, max response time per method execution and stores it in the corresponding maps
     */
    public static void generateMinMaxAvgResponseTimePerMethodExecution(){

        /**
         * save all perf times per invocation in a Map
         */
        Map<Integer, List<Long>> loadTimesPerInvocation = new HashMap<Integer, List<Long>>();
        for (String user : allPageLoadInfosPerVU.keySet()) {
            List<List<TimingInfo>> pageLoadInfosPerInvocation = allPageLoadInfosPerVU.get(user);
            // counter of invocations
            int i = 0;
            // iterate pageLoadInfos of every invocation a particular VU
            for (List<TimingInfo> timingInfos : pageLoadInfosPerInvocation) {
                i++;
                List<Long> loadTimes = new ArrayList<Long>();
                for (TimingInfo timingInfo : timingInfos) {
                    if (timingInfo != null) {
                        // avoid using perf times of dummy Entries, which are only used for CSV.Generation
                        String context = timingInfo.getContext();
                        if (context != null) {
                            Long loadDuration = timingInfo.getLoadDuration();
                            loadTimes.add(loadDuration);
                        }
                    }
                }
                // save List of loadtimes of invocation i in a Map
                if (loadTimesPerInvocation.containsKey(i)) {
                    List<Long> existingLoadTimes = loadTimesPerInvocation.get(i);
                    existingLoadTimes.addAll(loadTimes);
                }
                else {
                    loadTimesPerInvocation.put(i, loadTimes);
                }
            }
        }

        /**
         * generate min, max, avg and save them
         */
        for (Integer invocationNumber : loadTimesPerInvocation.keySet()) {
            List <Long> loadTimesOfInvocation = loadTimesPerInvocation.get(invocationNumber);

            /*
             * generate min, avg, max Value
             */
            Long averageResponseTimeOfTestInvocation = NumberUtils.getAverageValue(loadTimesOfInvocation);
            Long minResponseTimeOfTestInvocation = NumberUtils.getMinValue(loadTimesOfInvocation);
            Long maxResponseTimeOfTestInvocation = NumberUtils.getMaxValue(loadTimesOfInvocation);

            /*
             * save min, avg, max Value
             */
            averageResponseTimePerTestRepetition.put(invocationNumber, averageResponseTimeOfTestInvocation);
            maxResponseTimePerTestRepetition.put(invocationNumber, maxResponseTimeOfTestInvocation);
            minResponseTimePerTestRepetition.put(invocationNumber, minResponseTimeOfTestInvocation);
        }
    }

    /**
     * generates min, avg, max response times per test step and stores it in the corresponding maps
     */
    public static void generateMinMaxAvgResponseTimePerTestStep() {
        /**
         * save all perf times per Test steps in a Map
         */
        Map<Integer, List<Long>> loadTimesPerTestStep = new HashMap<Integer, List<Long>>();
        for (Long invocationNumber : pageLoadInfosPerTestMethodInvocation.keySet()) {
            List <TimingInfo> timingInfos = pageLoadInfosPerTestMethodInvocation.get(invocationNumber);

            // counter of Steps
            int i = 0;
            for (TimingInfo timingInfo : timingInfos) {
                i++;
                // avoid access to timingInfo for failed tests, that don't have this step
                if (timingInfo != null) {
                    Long loadDuration = timingInfo.getLoadDuration();

                    // save List of perf times of test step i in a Map
                    if (loadTimesPerTestStep.containsKey(i)) {
                        List<Long> existingLoadTimes = loadTimesPerTestStep.get(i);
                        existingLoadTimes.add(loadDuration);
                    }
                    else {
                        List<Long> loadTimes = new ArrayList<Long>();
                        loadTimes.add(loadDuration);
                        loadTimesPerTestStep.put(i, loadTimes);
                    }
                }
            }
        }

        /**
         * generate min, max, avg and save them
         */
        for (Integer invocationNumber : loadTimesPerTestStep.keySet()) {
            List <Long> loadTimesOfTestStep = loadTimesPerTestStep.get(invocationNumber);

           /*
            * generate min, avg, max Value
            */
            Long averageResponseTimeOfTestStep = NumberUtils.getAverageValue(loadTimesOfTestStep);
            Long minResponseTimeOfTestStep = NumberUtils.getMinValue(loadTimesOfTestStep);
            Long maxResponseTimeOfTestStep = NumberUtils.getMaxValue(loadTimesOfTestStep);

           /*
            * save min, avg, max Value
            */
            averageResponseTimePerTestStep.put(invocationNumber, averageResponseTimeOfTestStep);
            maxResponseTimePerTestStep.put(invocationNumber, maxResponseTimeOfTestStep);
            minResponseTimePerTestStep.put(invocationNumber, minResponseTimeOfTestStep);
        }
    }

    /**
     * sums up the status of all transactions and stores it in transactionStatus
     */
    public static void generateTransactionStatus() {
        if (pageLoadInfosPerTestMethodInvocation != null) {
            Set<Long> testMethodRepetitionNumbers = pageLoadInfosPerTestMethodInvocation.keySet();
            int successfulTransactions = 0;
            int failedTransactions = 0;
            int skippedTransactions = 0;
            for (Long repetitionNumber : testMethodRepetitionNumbers) {
                ITestResult iTestResult = PerfTestContainer.getTestResult(repetitionNumber);
                int testStatus = iTestResult.getStatus();
                //increment counters
                if (testStatus == ITestResult.SUCCESS) {
                    successfulTransactions++;
                }
                if (testStatus == ITestResult.SKIP) {
                    skippedTransactions++;
                }
                if (testStatus == ITestResult.FAILURE) {
                    failedTransactions++;
                }

            }
            transactionStatus.put(TEXT_TEST_SUCCESS, successfulTransactions);
            transactionStatus.put(TEXT_TEST_FAIL, failedTransactions);
            transactionStatus.put(TEXT_TEST_SKIP, skippedTransactions);
        }
    }

    /**
     * generate Responsetimehistory
     */
    public static void generateResponseTimeHistory() {
        if (pageLoadInfosPerTestMethodInvocation != null) {
            Set<Long> testMethodRepetitionNumbers = pageLoadInfosPerTestMethodInvocation.keySet();

            for (int i = 0; i < numberOfTestSteps; i++) {
                List<TimingInfo> timingInfos = new ArrayList<TimingInfo>();
                for (Long repetitionNumber : testMethodRepetitionNumbers) {
                    List<TimingInfo> timingInfoPerTransaction = pageLoadInfosPerTestMethodInvocation.get(repetitionNumber);
                    if (timingInfoPerTransaction.size() > i) {
                        TimingInfo timingInfo = timingInfoPerTransaction.get(i);
                        timingInfos.add(timingInfo);
                    }
                }
                allResponseTimesPerTestStep.put(i, timingInfos);
            }
        }
    }

    /**
     * generates Transactions per timeUnit
     *
     * @param timeUnit .
     */
    public static void generateTransactionThroughput(int timeUnit) {
        // pageLoadInfosPerVU is used because it contains time stamps of all finished test runs
        if (allPageLoadInfosPerVU != null) {
            Set<String> userNames = allPageLoadInfosPerVU.keySet();
            for (String userName : userNames) {
                List<List<TimingInfo>> responseTimesOfUser = allPageLoadInfosPerVU.get(userName);
                if (responseTimesOfUser != null) {
                    for (List<TimingInfo> timingInfosOfRepetition : responseTimesOfUser) {
                        for (TimingInfo timingInfoOfVU : timingInfosOfRepetition) {
                            String context = timingInfoOfVU.getContext();
                            if (context == null) {
                                /**
                                 * algorithm:
                                 * 1. take the timestamp of a completed test run, indicated with PageName=Test_End in pageLoadInfo
                                 * 2. compare with all the timestamps in allPageLoadInfosPerVU
                                 * 3. if timestamp1 == timestamp2 increment the counter for this Date to get all completed tests
                                 * for this particular point of time
                                 */

                                // 1. TimeStamp of Transaction
                                Long timeStampOfExecution = timingInfoOfVU.getTimeStamp();
                                switch (timeUnit) {
                                    case Calendar.SECOND:
                                        sumUpTransactionThroughputPerTimeUnit(timeStampOfExecution, timeUnit, transactionsPerSecond);
                                        break;

                                    case Calendar.MINUTE:
                                        sumUpTransactionThroughputPerTimeUnit(timeStampOfExecution, timeUnit, transactionsPerMinute);
                                        break;

                                    case Calendar.HOUR_OF_DAY:
                                        sumUpTransactionThroughputPerTimeUnit(timeStampOfExecution, timeUnit, transactionsPerHour);
                                        break;

                                    default:
                                        throw new TesterraRuntimeException("TransactionThroughput is not supported for the given timeUnit");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * counts the Transaction for the given date regarding the timeUnit
     *
     * @param timeStampOfMeasurement .
     * @param timeUnit .
     * @param transactionsPerTimeUnit .
     */
    private static void sumUpTransactionThroughputPerTimeUnit(Long timeStampOfMeasurement, int timeUnit,
                                                              Map<Long, Integer> transactionsPerTimeUnit) {
        Set<Long> timeStamps = transactionsPerTimeUnit.keySet();
        boolean alreadyExisting = false;
        // 2. iterate all existing TimeStamps of Requests
        for (Long timeStamp : timeStamps) {
            // 3. if the timeStampOfTransaction equals one of all TimeStamps of Request, the counter has to be incremented
            if (compareTimeStamps(timeStampOfMeasurement, timeStamp, timeUnit)) {
                int amount = transactionsPerTimeUnit.get(timeStamp);
                amount++;
                transactionsPerTimeUnit.put(timeStamp, amount);
                alreadyExisting = true;
                break;
            }
        }
        if (!alreadyExisting) {
            transactionsPerTimeUnit.put(timeStampOfMeasurement, 1);
        }
    }

    /**
     * compares given Dates via Calendar
     *
     * @param timeStampOfMeasurement .
     * @param timeStampInTransactionMap .
     * @param timeUnit .
     * @return checked dates
     */
    public static boolean compareTimeStamps(Long timeStampOfMeasurement, Long timeStampInTransactionMap, int timeUnit) {
        switch (timeUnit) {
            // transform timeStamp from ms to second
            case Calendar.SECOND:
                timeStampOfMeasurement = TimeUnit.MILLISECONDS.toSeconds(timeStampOfMeasurement);
                timeStampInTransactionMap =  TimeUnit.MILLISECONDS.toSeconds(timeStampInTransactionMap);
                break;
            // transform timeStamp from ms to minute
            case Calendar.MINUTE:
                timeStampOfMeasurement = TimeUnit.MILLISECONDS.toMinutes(timeStampOfMeasurement);
                timeStampInTransactionMap = TimeUnit.MILLISECONDS.toMinutes(timeStampInTransactionMap);
                break;
            // transform timeStamp from ms to hour
            case Calendar.HOUR_OF_DAY:
                timeStampOfMeasurement = TimeUnit.MILLISECONDS.toHours(timeStampOfMeasurement);
                timeStampInTransactionMap = TimeUnit.MILLISECONDS.toHours(timeStampInTransactionMap);
                break;

            // Exception
            default:
                throw new TesterraRuntimeException("Camparison not supported for the given timeUnit");
        }

        if (timeStampOfMeasurement.equals(timeStampInTransactionMap)) {
            return true;
        }

        return false;
    }

    /**
     * generates requests per TimeUnit
     *
     * @param timeUnit .
     */
    public static void generateRequestThroughput(int timeUnit) {

        if (pageLoadInfosPerTestMethodInvocation != null) {
            Set<Long> testMethodRepetitionNumbers = pageLoadInfosPerTestMethodInvocation.keySet();
            for (Long repetitionNumber : testMethodRepetitionNumbers) {
                List<TimingInfo> timingInfoPerTransaction = pageLoadInfosPerTestMethodInvocation.get(repetitionNumber);
                for (TimingInfo timingInfoOfTransaction : timingInfoPerTransaction) {

                    /**
                     * algorithm:
                     * 1. take the TimeStamps of a measurement of a Request
                     * 2. compare with all the TimeStamps in requestsPerSecond
                     * 3. if TimeStamp1==TimeStamp2 increment the count für this TimeStamps to get all requests for this particular point of time
                     */
                    // 1. TimeStamp of Request
                    Long timeStampOfMeasurement = timingInfoOfTransaction.getTimeStamp();
                    switch (timeUnit) {
                        case Calendar.SECOND:
                            sumUpRequestThroughputPerTimeUnit(timeStampOfMeasurement, timeUnit, requestsPerSecond);
                            break;

                        case Calendar.MINUTE:
                            sumUpRequestThroughputPerTimeUnit(timeStampOfMeasurement, timeUnit, requestsPerMinute);
                            break;

                        case Calendar.HOUR_OF_DAY:
                            sumUpRequestThroughputPerTimeUnit(timeStampOfMeasurement, timeUnit, requestsPerHour);
                            break;

                        // Exception
                        default:
                            throw new TesterraRuntimeException("RequestThroughput is not supported for the given timeUnit");

                    }
                }
            }
        }
    }

    /**
     * * counts the Requests for the given date regarding the timeUnit
     *
     * @param timeStampOfMeasurement .
     * @param timeUnit .
     * @param requestsPerTimeUnit .
     */
    private static void sumUpRequestThroughputPerTimeUnit(Long timeStampOfMeasurement, int timeUnit,
                                                          Map<Long, Integer> requestsPerTimeUnit) {
        Set<Long> timeStamps = requestsPerTimeUnit.keySet();
        boolean alreadyExisting = false;
        // 2. iterate all existing TimeStamps of Requests
        for (Long timeStamp : timeStamps) {
            // 3. if the timeStampOfMeasurement equals one of all TimeStamps of Request, the counter has to be incremented
            if (compareTimeStamps(timeStampOfMeasurement, timeStamp, timeUnit)) {
                int amount = requestsPerTimeUnit.get(timeStamp);
                amount++;
                requestsPerTimeUnit.put(timeStamp, amount);
                alreadyExisting = true;
                break;
            }
        }
        if (!alreadyExisting) {
            requestsPerTimeUnit.put(timeStampOfMeasurement, 1);
        }
    }

    /**
     * sorts a given Map of PageloadInfos of different threads via Date, increasing
     *
     * @param map .
     * @return sortedMap
     */
    public static Map<Long, List<TimingInfo>> sortMap(Map<Long, List<TimingInfo>> map) {
        List<Map.Entry<Long, List<TimingInfo>>> list = new LinkedList<Map.Entry<Long, List<TimingInfo>>>(map.entrySet());

        // comparator: time of the first measurements of every entry
        Collections.sort(list, (o1, o2) -> {
            //Prepare first Entry for accessing dateFirstEntry
            List<TimingInfo> valueOfEntryObject1 = o1.getValue();
            Long timeStampFirstEntry = valueOfEntryObject1.get(0).getTimeStamp();

            //Prepare second Entry for accessing dateSecondEntry
            List<TimingInfo> valueOfEntryObject2 = o2.getValue();
            Long timeStampSecondEntry = valueOfEntryObject2.get(0).getTimeStamp();

            int result = timeStampFirstEntry.compareTo(timeStampSecondEntry);
            return result;
        });
        HashMap<Long, List<TimingInfo>> sortedHashMap = new LinkedHashMap<Long, List<TimingInfo>>();
        for (Map.Entry<Long, List<TimingInfo>> mapEntry : list) {
            sortedHashMap.put(mapEntry.getKey(), mapEntry.getValue());
        }
        return sortedHashMap;
    }

//    /**
//     * create the String of the Destinationfolder for CSVs, Graphs and Har-files
//     */
//    public static void setOutputFolderName() {
//        Date startDate = new Date();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy_HH-mm-ss");
//        //use start time of the Test as unique folder name
//        String formatedDate = simpleDateFormat.format(startDate);
//        destinationFolder = "Perf_Test_Results\\Test_" + formatedDate + "_Uhr\\";
//    }
//
//    /**
//     * saves test parameters of a perf test in a CSV-File
//     */
//    public static void saveTestConfigs() {
//        makeSubDirectories(SUB_FOLDER_TEST_CONFIG);
//        // main config, same for all executed methods
//        String browser = WebDriverManager.config().getBrowser().name();
//        String wedDriverMode = WebDriverManager.config().getWebDriverMode();
//        String reuse = "" + Flags.REUSE_DATAPROVIDER_DRIVER_BY_THREAD;
//        String thinkTime = "" + PropertyManager.getIntProperty(TesterraProperties.PERF_PAGE_THINKTIME_MS, 0);
//        String server = PropertyManager.getProperty(TesterraProperties.SELENIUMHOST);
//
//        ITestNGMethod[] executedTestmethods = PerfTestContainer.getTestContext().getAllTestMethods();
//        for (ITestNGMethod testMethod : executedTestmethods) {
//            String methodName = testMethod.getMethodName();
//
//            // method specific parameters
//            String amountOfVUString = "" + allPageLoadInfosPerVU.size();
//            String invocationCount = "" + testMethod.getInvocationCount();
//
//            try {
//                FileWriter writer = new FileWriter(destinationFolder + methodName + "\\" + SUB_FOLDER_TEST_CONFIG + "\\testconfig.csv", true);
//                // append config parameters to file
//                writer.append("Amount of User = ").append(amountOfVUString).append("\n");
//                writer.append("Invocations = ").append(invocationCount).append("\n");
//                writer.append("Thinktime-Base = ").append(thinkTime).append(" ms\n");
//                writer.append("Driver-Reuse = ").append(reuse).append("\n");
//                writer.append("Browser = ").append(browser).append("\n");
//                writer.append("Mode = ").append(wedDriverMode).append("\n");
//                writer.append("IP-Address = ").append(server).append("\n");
//                writer.append("Duration = ").append(TestStatistics.getInstanceForTests().getDuration()).append("\n");
//
//                // save file
//                writer.flush();
//                writer.close();
//
//            } catch (IOException e) {
//                throw new TesterraSystemException("Error while saving the test configuration after perf test");
//            }
//        }
//    }

//    /**
//     * create graphs for all test methods
//     * @param pageLoadInfosPerTestMethod .
//     */
//    public static void processLoadTestDataForGraphs(Map<String, Map<Long, List<TimingInfo>>> pageLoadInfosPerTestMethod) {
//        if (pageLoadInfosPerTestMethod != null) {
//            PerfTestGraphGenerator generator = new PerfTestGraphGenerator();
//            try {
//                generator.generateAllGraphs(pageLoadInfosPerTestMethod);
//            } catch (IOException e) {
//                throw new TesterraSystemException("Error while saving Load Test Report Graphics");
//            }
//        }
//    }

//    /**
//     * write pageLoadInfos in a CSV for each VU
//     * @param pageLoadInfos .
//     */
//    public static void processLoadTestDataForCSV(Map<Long, List<TimingInfo>> pageLoadInfos) {
//        if (pageLoadInfos != null) {
//            Map<Long, List<TimingInfo>> sortedPageLoadInfos = PerfTestReportUtils.sortMap(pageLoadInfos);
//            saveResultsToCSV(sortedPageLoadInfos);
//        }
//    }

    /**
     * creates a map Map which holds all Measurement of all threads
     * @param pageLoadInfosPerTestMethod .
     * @return pageLoadInfos
     */
    public static Map<Long, List<TimingInfo>> createPageLoadInfosOfAllThreads(Map<String,
        Map<Long, List<TimingInfo>>> pageLoadInfosPerTestMethod) {

        Set<String> keys = pageLoadInfosPerTestMethod.keySet();
        Map<Long, List<TimingInfo>> pageLoadInfos = new HashMap<Long, List<TimingInfo>>();
        for (String testMethodName : keys) {
            pageLoadInfos.putAll(pageLoadInfosPerTestMethod.get(testMethodName));
        }
        return pageLoadInfos;
    }

    // _____________Generation_of_Metrics_per_Action________________

    /**
     * creates a map which holds all Measurement for each Action
     * @param pageLoadInfosPerTestMethod .
     * @return pageLoadInfos
     */
    public static void createPageLoadInfosOfAllActions(Map<String,
        Map<Long, List<TimingInfo>>> pageLoadInfosPerTestMethod) {

        Set<String> testMethodNames = pageLoadInfosPerTestMethod.keySet();
        Map<String, List<TimingInfo>> pageLoadInfosOfAllActions = new HashMap<String, List<TimingInfo>>();
        // iterate all executed testmethods
        for (String testMethodName : testMethodNames) {
            Map<Long, List<TimingInfo>> pageLoadInfosPerThreadMap = pageLoadInfosPerTestMethod.get(testMethodName);
            Set<Long> threadIDs = pageLoadInfosPerThreadMap.keySet();
            // iterate all threads of a specific test method
            for(Long threadID : threadIDs) {
                List<TimingInfo> timingInfosOfThread = pageLoadInfosPerThreadMap.get(threadID);
                // iterate all measurements of this thread (=invocation)
                for(TimingInfo timingInfo : timingInfosOfThread) {
                    String actionName = timingInfo.getContext();

                    if(actionName == null) {
                        actionName = "Call Of startURL";
                    }
                    // List of pageLoadInfos of a specific action
                    List<TimingInfo> pageLoadOfSpecificAction = pageLoadInfosOfAllActions.get(actionName);
                    // no list existent
                    if (pageLoadOfSpecificAction == null) {
                        // creat list
                        pageLoadOfSpecificAction = new ArrayList<TimingInfo>();
                        // add measurement to list
                        pageLoadOfSpecificAction.add(timingInfo);
                    } else {
                        pageLoadOfSpecificAction.add(timingInfo);
                    }
                    // add list of measurement to the action map
                    pageLoadInfosOfAllActions.put(actionName, pageLoadOfSpecificAction);
                }
            }
        }
        pageLoadInfosPerTestStepAction = pageLoadInfosOfAllActions;
    }


    public static void generateAverageMinMaxPerAction (){
        Map<String, List<Long>> loadTimesPerTestStepAction = new HashMap<String, List<Long>>();

        Set<String> actionNames = pageLoadInfosPerTestStepAction.keySet();
        // get all ResponseTimes Per Action for generation of min, max and avg
        for(String actionName : actionNames) {
            List<TimingInfo> measurementsOfSpecificiAction =  pageLoadInfosPerTestStepAction.get(actionName);
            for(TimingInfo measurement : measurementsOfSpecificiAction) {
                if(measurement != null) {
                    Long loadDuration = measurement.getLoadDuration();
                    if (loadTimesPerTestStepAction.containsKey(actionName)) {
                        List<Long> existingLoadTimes = loadTimesPerTestStepAction.get(actionName);
                        existingLoadTimes.add(loadDuration);
                    } else {
                        List<Long> loadTimes = new ArrayList<Long>();
                        loadTimes.add(loadDuration);
                        loadTimesPerTestStepAction.put(actionName, loadTimes);
                    }
                }
            }
        }

        /**
         * generate min, max, avg and save them
         */
        for (String actionName : loadTimesPerTestStepAction.keySet()) {
            List <Long> loadTimesOfTestStep = loadTimesPerTestStepAction.get(actionName);

           /*
            * generate min, avg, max Value
            */
            Long averageResponseTimeOfTestStep = NumberUtils.getAverageValue(loadTimesOfTestStep);
            Long minResponseTimeOfTestStep = NumberUtils.getMinValue(loadTimesOfTestStep);
            Long maxResponseTimeOfTestStep = NumberUtils.getMaxValue(loadTimesOfTestStep);

           /*
            * save min, avg, max Value
            */
            averageResponseTimePerTestStepAction.put(actionName, averageResponseTimeOfTestStep);
            maxResponseTimePerTestStepAction.put(actionName, maxResponseTimeOfTestStep);
            minResponseTimePerTestStepAction.put(actionName, minResponseTimeOfTestStep);
        }
    }


    //________________Output_Generation______________________

//    /**
//     * Writes given Measurements to a CSV with a format compliant for mpAnalyse
//     *
//     * @param pageLoadInfos
//     */
//    private static void saveResultsToCSV(Map<Long, List<TimingInfo>> pageLoadInfos) {
//        makeSubDirectories(SUB_FOLDER_OF_CSV);
//        try {
//            Set<Long> idSet = pageLoadInfos.keySet();
//            for (Long threadId : idSet) {
//                ITestResult iTestResult = PerfTestContainer.getTestResult(threadId);
//                // testmethodname for the given testresult
//                String methodName = iTestResult.getMethod().getMethodName().concat("\\");
//                String threadName = PerfTestContainer.getThreadNameByID(threadId);
//                FileWriter writer = new FileWriter(destinationFolder + methodName + SUB_FOLDER_OF_CSV +
//                        "\\results_of_" + threadName + ".csv", true);
//
//                List<TimingInfo> pageLoadInfoPerThread = pageLoadInfos.get(threadId);
//                for (TimingInfo pageLoadInfo : pageLoadInfoPerThread) {
//                    long timeStampMilli = pageLoadInfo.getTimeStamp();
//                    long unixTimeStamp = TimeUnit.MILLISECONDS.toSeconds(timeStampMilli);
//                    writer.append(String.valueOf(unixTimeStamp)).append(";");
//                    writer.append("null;");
//                    String hostAddress = iTestResult.getHost();
//                    if (hostAddress == null) {
//                        writer.append("local;");
//                    } else {
//                        writer.append(hostAddress).append(";");
//                    }
//
//                    Class page = pageLoadInfo.getPage();
//                    // in case of endTime of test (dummy pageloadInfo) page is null
//                    if(page != null) {
//                        writer.append("GET_").append(page.getSimpleName()).append("_").append(threadName).append(";");
//                    } else {
//                        writer.append("GET_").append(TEST_END).append("_").append(threadName).append(";");
//                    }
//                        writer.append("LT;");
//                        writer.append(String.valueOf(pageLoadInfo.getLoadDuration()));
//                        writer.append("\n");
//
//                }
//
//                // reached the endTime of the LoadTimes of a specific VU, therefore we need to save the TA-line,
//                // to indicate the endTime of measurements and the teststatus
//                long testRunEndTime = iTestResult.getEndMillis();
//                long timeStamp = TimeUnit.MILLISECONDS.toSeconds(testRunEndTime);
//                String testMethodName = iTestResult.getMethod().getMethodName();
//                writer.append(String.valueOf(timeStamp)).append(";");
//                writer.append("null;");
//                String hostAddress = System.getProperty(TesterraProperties.SELENIUMHOST, "local");
//                writer.append(hostAddress).append(";");
//                writer.append("End_of_").append(testMethodName).append("_").append(threadName).append(";");
//                writer.append("TA;");
//
//                if (iTestResult.isSuccess()) {
//                    writer.append("Ok");
//                } else {
//                    writer.append("Fail");
//                }
//                writer.append("\n");
//                // put Data into CSV
//                writer.flush();
//                //close Stream
//                writer.close();
//            }
//        } catch (IOException e) {
//            throw new TesterraSystemException("Error while saving the CSV file");
//        }
//    }

//    /**
//     * creates all subdirectories for saving CSV to each testmethod
//     *
//     * @param subDirectoryName
//     */
//    private static void makeSubDirectories(String subDirectoryName) {
//        // Testcontext contrains all TestMethods, for every methodename there needs to be a separate folder
//        ITestNGMethod[] executedTestmethods = PerfTestContainer.getTestContext().getAllTestMethods();
//        // iterate all executed testmethods to make a folder
//        for (ITestNGMethod testmethod : executedTestmethods) {
//            String testMethodName = testmethod.getMethodName().concat("\\");
//            // create subfolder for every testmethod
//            File resultsSubFolder = new File(destinationFolder + testMethodName + subDirectoryName);
//            if (!resultsSubFolder.exists()) {
//                resultsSubFolder.mkdirs();
//            }
//        }
//    }


    //===========Getter================
    public static Map<Long, Integer> getRequestsPerSecond() {
        return requestsPerSecond;
    }

    public static Map<Long, Integer> getRequestsPerMinute() {
        return requestsPerMinute;
    }

    public static Map<Long, Integer> getRequestsPerHour() {
        return requestsPerHour;
    }

    public static Map<Long, Integer> getTransactionsPerSecond() {
        return transactionsPerSecond;
    }

    public static Map<Long, Integer> getTransactionsPerMinute() {
        return transactionsPerMinute;
    }

    public static Map<Long, Integer> getTransactionsPerHour() {
        return transactionsPerHour;
    }

    public static Map<String, List<List<TimingInfo>>> getAllPageLoadInfosPerVU() {
        return allPageLoadInfosPerVU;
    }

    public static Map<Long, List<TimingInfo>> getPageLoadInfosPerTestMethodInvocation() {
        return pageLoadInfosPerTestMethodInvocation;
    }

    public static Map<Integer, Long> getAverageResponseTimePerTestStep() {
        return averageResponseTimePerTestStep;
    }

    public static Map<Integer, Long> getMaxResponseTimePerTestStep() {
        return maxResponseTimePerTestStep;
    }

    public static Map<Integer, Long> getMinResponseTimePerTestStep() {
        return minResponseTimePerTestStep;
    }

    public static Map<String, Integer> getTransactionStatus() {
        return transactionStatus;
    }

    public static Map<Integer, List<TimingInfo>> getAllResponseTimesPerTestStep() {
        return allResponseTimesPerTestStep;
    }

    public static Map<Integer, Long> getAverageResponseTimePerTestRepetition() {
        return averageResponseTimePerTestRepetition;
    }

    public static Map<Integer, Long> getMaxResponseTimePerTestRepetition() {
        return maxResponseTimePerTestRepetition;
    }

    public static Map<Integer, Long> getMinResponseTimePerTestRepetition() {
        return minResponseTimePerTestRepetition;
    }

    public static String getDestinationFolder() {
        return destinationFolder;
    }

    /**
     * get the TestName via the first Entry of the raw pageLoadInfos
     * assumption: all entreis belong to one testmethod, which means different testmethods in parallel aren't supported yet
     *
     * @param pageLoadInfosRaw .
     * @return test name
     */
    public static String getTestName(Map<Long, List<TimingInfo>> pageLoadInfosRaw) {
        String testName;
        Long firstThreadId = pageLoadInfosRaw.entrySet().iterator().next().getKey();
        testName = PerfTestContainer.getTestResult(firstThreadId).getName();
        return testName;
    }

    public static Map<String, List<TimingInfo>> getPageLoadInfosPerTestStepAction() {
        return pageLoadInfosPerTestStepAction;
    }

    public static Map<String, Long> getAverageResponseTimePerTestStepAction() {
        return averageResponseTimePerTestStepAction;
    }

    public static Map<String, Long> getMaxResponseTimePerTestStepAction() {
        return maxResponseTimePerTestStepAction;
    }

    public static Map<String, Long> getMinResponseTimePerTestStepAction() {
        return minResponseTimePerTestStepAction;
    }

    public static int getNumberOfTestSteps() {
        return numberOfTestSteps;
    }
}
