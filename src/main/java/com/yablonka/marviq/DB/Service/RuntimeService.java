package com.yablonka.marviq.DB.Service;

import com.yablonka.marviq.DB.dao.RuntimeRepository;
import com.yablonka.marviq.DB.model.Runtime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class RuntimeService {
    @Autowired
    private RuntimeRepository runtimeRepository;

    private Logger LOG = Logger.getLogger("RuntimeService");

    //  PRIVATE method that returns a List of Machine-Runtime-Data for a specific date
    private List<Runtime> findMachineRuntimesByDate(String machineName, String date) {
        LOG.log(Level.INFO, "in findMachineRuntimesByLastDay");
        List<Runtime> machineStatuses = runtimeRepository.findAllByMachineName(machineName);
        //here you should filter only to last day
        LOG.log(Level.INFO, "date  for first cell is " + machineStatuses.get(0).getDateTime());
        LOG.log(Level.INFO, "isRunning  for first cell is " + machineStatuses.get(0).getIsRunning());
        List<Runtime> machineStatusesLastDay = new ArrayList<>();
        for (Runtime runtime : machineStatuses) {
            if (runtime.getDateTime().contains(date)) {
                machineStatusesLastDay.add(runtime);
            }
        }
        return machineStatusesLastDay;
    }

    //    PRIVATE method to change a String that has a Date into a String with format HH:M
    private String changeToTimeOnly(String dateTime) {
        int index = dateTime.indexOf(':');
        if (dateTime.charAt(index - 2)  == ' ') {
            dateTime = dateTime.substring(index - 1,dateTime.lastIndexOf(':'));
        } else {
            dateTime = dateTime.substring(index - 2,dateTime.lastIndexOf(':'));
        }
        LOG.log(Level.INFO, "dateTime is " + dateTime);
        return dateTime;
    }

    //    PRIVATE method that returns a List with an updated (more-comfortable) time format of HH:MM
    private List<Runtime> changeTimeFormat(List<Runtime> runtimeData) {
        for (Runtime runtime : runtimeData) {
            String updatedDatetime = runtime.getDateTime();
            LOG.log(Level.INFO, "runtime.getDateTime() is " + updatedDatetime);
            updatedDatetime = changeToTimeOnly(updatedDatetime);
            LOG.log(Level.INFO, "updatedTime is " + updatedDatetime);
            runtime.setDateTime(updatedDatetime);
        }
        return runtimeData;
    }

    //    PRIVATE method that returns the result of the time difference between two different Time Strings
    //    (DUPLICATED from ProductionService class)
    private int subtractTimes(String firstTime, String secondTime) {
        int firstIndex = firstTime.indexOf(':');
        int secondIndex = secondTime.indexOf(':');
        String firstHour = firstTime.substring(0,firstIndex);
        String firstMinute = firstTime.substring(firstIndex + 1);
        String secondHour = secondTime.substring(0,secondIndex);
        String secondMinute = secondTime.substring(secondIndex + 1);
        int numFirstHour = Integer.parseInt(firstHour);
        int numFirstMinute = Integer.parseInt(firstMinute);
        int numSecondHour = Integer.parseInt(secondHour);
        int numSecondMinute = Integer.parseInt(secondMinute);
        return Math.abs(numFirstHour * 60 + numFirstMinute - (numSecondHour * 60 + numSecondMinute));
    }

    //      returns an int of number of Total Minutes the machine was down for a specific date
    public int calculateDowntimePercentage(String machineName, String date) {
        LOG.log(Level.INFO, "in calculateDowntimePercentage");
        List<Runtime> runtimeData = findMachineRuntimesByDate(machineName, date);
        LOG.log(Level.INFO, "date  for first cell is " + runtimeData.get(0).getDateTime());
        LOG.log(Level.INFO, "isRunning  for first cell is " + runtimeData.get(0).getIsRunning());
        runtimeData = changeTimeFormat(runtimeData);
//        LOG.log(Level.INFO,"date for first cell is " + runtimeData.get(0).getDateTime());
        for (int i=0; i < runtimeData.size(); i++) {
            LOG.log(Level.INFO, "dateTime is " + runtimeData.get(i).getDateTime());
            LOG.log(Level.INFO,"isRunning is " + runtimeData.get(i).getIsRunning());
        }
        int timeSum = 0;
        String tempTime = "0:00";
        for (Runtime runtime : runtimeData) {
            if (runtime.getIsRunning() == 0) {
                tempTime = runtime.getDateTime();
            } else {
                timeSum += subtractTimes(tempTime,runtime.getDateTime());
            }
        }
        Runtime runtime = runtimeData.get(runtimeData.size() -1);
        if (runtime.getIsRunning() == 0) {
            timeSum += subtractTimes("23:59",runtime.getDateTime()) + 1;
        }
        LOG.log(Level.INFO, "downtime percentage is " + (float) timeSum / (24*60));
        return timeSum;
    }
}
