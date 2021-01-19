package com.yablonka.marviq.DB.Service;

import com.yablonka.marviq.DB.dao.ProductionRepository;
import com.yablonka.marviq.DB.model.Production;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProductionService {
    @Autowired
    private ProductionRepository productionRepository;

    private Logger LOG = Logger.getLogger("ProductionService");

//  PRIVATE method that returns a List of Machine-Production-Data by date
    private List<Production> findMachineStatusByDate(String machineName, String variableName, String date) {
        LOG.log(Level.INFO, "date is " + date);
        LOG.log(Level.INFO, "in findMachineStatusLastDay");
        List<Production> machineStatuses = productionRepository.findAllByMachineNameAndVariableName(machineName,variableName);
        //here you should filter only to last day
        LOG.log(Level.INFO, "date  for first cell is " + machineStatuses.get(0).getFromDateTime());
        List<Production> machineStatusesLastDay = new ArrayList<>();
        for (Production production : machineStatuses) {
            if (production.getFromDateTime().contains(date)) {
                machineStatusesLastDay.add(production);
            }
        }
        LOG.log(Level.INFO, "length of machineStatusesLastDay is " + machineStatusesLastDay.size());
        return machineStatusesLastDay;
    }

//    returns int of Total-Values per requested String variableName for specific machine for specific date
    public int calculateGrossVariable(String machineName, String variableName, String date) {
        List<Production> productions = findMachineStatusByDate(machineName, variableName, date);
        int sum = 0;
        for (Production production : productions) {
            sum += production.getValue();
        }
        LOG.log(Level.INFO,"output for variableName " + variableName + " is " + sum);
        return sum;
    }

//    PRIVATE method that returns Array of 24 int cells containing the Hourly-Production of the Machine by subtracting the scrap
//    value from the production value for each specific time for a specific date
    private int[] calculateHourlyProduction(String machineName, String date) {
        List<Production> productionData = findMachineStatusByDate(machineName, "PRODUCTION", date);
        List<Production> scrapData = findMachineStatusByDate(machineName, "SCRAP", date);
        LOG.log(Level.INFO, "productionData length is " + productionData.size());
        LOG.log(Level.INFO, "scrapData length is " + scrapData.size());
        int[] machineHourlyNetProduction = new int[24];
        int counter = 0;
        int hourProduction = 0;
        while (counter < productionData.size()) {
            Production production = productionData.get(counter);
            Production scrap = scrapData.get(counter);
            hourProduction += production.getValue() - scrap.getValue();
            if (((counter + 1) % 12 == 0)) {
                machineHourlyNetProduction[((counter + 1) / 12) - 1] = hourProduction;
                hourProduction = 0;
            }
            counter++;
        }

        for (int i=0; i<24; i++) {
            LOG.log(Level.INFO, "cell " + i + " is " + machineHourlyNetProduction[i]);
        }
        return machineHourlyNetProduction;
    }

//    returns Array of 24 int cells by date for Hourly-Production of the Machine using the 'calculateHourlyProduction' method
    public int[] calculateHourlyNetProduce(String machineName, String date) {
        int[] hourlyNetProduce = calculateHourlyProduction(machineName, date);
        return  hourlyNetProduce;
    }

//    returns the Total-Net-Production of the Machine calling the method that creates an Array containing the Net-Production
//    of the specific Machine.
//    public int calculateTtlNetProduction(String machineName) {
//        int[] hourlyProduction = calculateHourlyProduction(machineName);
//        int sum=0;
//        for (int i=0; i<hourlyProduction.length; i++) {
//            sum += hourlyProduction[i];
//        }
//        LOG.log(Level.INFO, "total production is " + sum);
//        return sum;
//    }

//    accepts a 24-celled Array containing the Hourly-Net-Production of the Machine and returns an int of the
//    Machine's Total-Net-Production
    public int calculateTtlNetProduction(int[] hourlyProduction) {
        int sum=0;
        for (int i=0; i<hourlyProduction.length; i++) {
            sum += hourlyProduction[i];
        }
        LOG.log(Level.INFO, "total production is " + sum);
        return sum;
    }

//      returns Scrap-Percentage in relation to the Total-Production of the Machine per date
    public float calculateScrapPercentage(String machineName, String date) {
        List<Production> productions = findMachineStatusByDate(machineName, "PRODUCTION", date);
        List<Production> scraps = findMachineStatusByDate(machineName, "SCRAP", date);
        int sumProduction = 0;
        int sumScrap = 0;
        for (int i=0; i<productions.size(); i++) {
            sumProduction += productions.get(i).getValue();
            sumScrap += scraps.get(i).getValue();
        }
        LOG.log(Level.INFO, "scrap percentage is " + (float) sumScrap / sumProduction);
        return (float) sumScrap / sumProduction;
    }

//    PRIVATE method to change a String that has a Date into a String with format HH:MM
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
    private List<Production> changeTimeFormat(List<Production> productionData) {
//        List<Runtime> updatedRuntimeData = new ArrayList<>();
        for (Production production : productionData) {
            String updatedFromDateTime = production.getFromDateTime();
            String updatedToDateTime = production.getToDateTime();
//            LOG.log(Level.INFO, "runtime.getDateTime() is " + updatedDatetime);
            updatedFromDateTime = changeToTimeOnly(updatedFromDateTime);
            updatedToDateTime = changeToTimeOnly(updatedToDateTime);
//            LOG.log(Level.INFO, "updatedTime is " + updatedDatetime);
            production.setFromDateTime(updatedFromDateTime);
            production.setToDateTime(updatedToDateTime);
        }
        return productionData;
    }

//    PRIVATE method that returns the result of the time difference between two different Time Strings
//    (DUPLICATED from RuntimeService class)
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

//    returns a Color variable that is GREEN if core temperature of Machine was below or equal to 85 C, ORANGE if core
//    temperature was between 85 and 100 C for 15 minutes or more and RED if core temperature ever reached 100 C
    public Color findCoreTemp(String machineName, String date) {
        List<Production> machineTemp = findMachineStatusByDate(machineName, "CORE TEMPERATURE", date);
        machineTemp = changeTimeFormat(machineTemp);
        Color color = Color.GREEN;
        String tempTime = "0:00";
        Boolean orangeDanger = false;
        for (Production production : machineTemp) {
            if (production.getValue() <= 85) {
                orangeDanger = false;
            } else if (production.getValue() > 85 && production.getValue() <= 100) {
                if (orangeDanger) {
                    if (subtractTimes(production.getToDateTime(), tempTime) > 15) {
                        color = Color.ORANGE;
                        LOG.log(Level.INFO, "color is now orange because temp was between 85 and 100 starting at " + tempTime
                        + " up until " + production.getToDateTime());
                    }
                } else {
                    orangeDanger = true;
                    tempTime = production.getFromDateTime();
                }
            } else if (production.getValue() > 100) {
                color = Color.RED;
                LOG.log(Level.INFO, "color is now red because at time between " + production.getFromDateTime() + " and " + production.getToDateTime() +
                " temp is " + production.getValue());
            }
        }
        return color;
    }
}
