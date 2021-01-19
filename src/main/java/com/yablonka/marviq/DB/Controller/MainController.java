package com.yablonka.marviq.DB.Controller;

import com.yablonka.marviq.DB.Service.ProductionService;
import com.yablonka.marviq.DB.Service.RuntimeService;
import com.yablonka.marviq.DB.model.Form;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class MainController {
    @Autowired
    private ProductionService productionService;
    @Autowired
    private RuntimeService runtimeService;

    private Logger LOG = Logger.getLogger("Controller");

//  GET method where user gets to choose the machine and date on which to view the statuses for his desired action
    @RequestMapping("/")
    public String home(Model model) {
        List<String> machines = new ArrayList<>();
        machines.add("2x2 brick mould");
        machines.add("3x2 brick mould");
        machines.add("4x2 brick mould");
//        String date = "07/01/2018";
//        model.addAttribute("date",date);
        model.addAttribute("machines",machines);
        Form form = new Form();
        model.addAttribute(form);
        return "home";
    }

//  POST method where user's decision is inputted. it automatically redirects user for his desired url.
    @RequestMapping(value = "/", method=RequestMethod.POST)
    public String selected(Model model, @RequestParam("machineName") String machine, @RequestParam("date") String date) {
        machine = machine.replace(" ", "_");
        LOG.log(Level.INFO, "machineName is " + machine + ", date is " + date);
        return String.format("redirect:/%s/%s", date, machine);
    }

//  User views his chosen machine's daily parameters and status over the last day
    @RequestMapping("/{date}/{machine}")
    public String machine(@PathVariable String date, @PathVariable String machine, Model model) {
        LOG.log(Level.INFO, "machineName is " + machine + ", date is " + date);
        machine = machine.replace("_", " ");
        LOG.log(Level.INFO, "machineName is " + machine + ", date is " + date);
        model.addAttribute("machine",machine);
        model.addAttribute("date", date);

//       returns 24-celled int Array of hourly net produce for each hour of the day for the machine
        int[] hours = productionService.calculateHourlyNetProduce(machine,date);
//        returns Total-Net-Production for the machine
        int ttlNetProduction = productionService.calculateTtlNetProduction(hours);
//        returns ratio of Scrap-to-Net production
        float scrapPercentage = productionService.calculateScrapPercentage(machine,date);

//        returns int for total minutes of downtime for the machine
        int downtime = runtimeService.calculateDowntimePercentage(machine,date);
//        calculates ratio for downtime per total minutes per day
        float downtimePercentage = (float) downtime/ (24 * 60);

        LOG.log(Level.INFO, "downtime is " + downtime);

//        adding values to model for Thymeleaf - Assignment A
        model.addAttribute("hours",hours);
        model.addAttribute("ttlNetProduction", ttlNetProduction);
        model.addAttribute("scrapPercentage", scrapPercentage);
        model.addAttribute("downtimePercentage",downtimePercentage);

//         returns color indication of machine-status as per Core-Temperature instructions in Assignment B.
//         Color class is used (and not String) for future optimization of the code
        Color color = productionService.findCoreTemp(machine, date);

//        defining String to correlate returned color for easier usage in Thymeleaf.
        String stringColor = "green";
        if (color == Color.RED) {
            stringColor = "red";
        } else if (color == Color.ORANGE) {
            stringColor = "orange";
        }

//        adding color value to model for Thymeleaf - Assignment B
        model.addAttribute("color", stringColor);

//        returns Total Gross Production in the last day for machine
        int ttlGrossProduction = productionService.calculateGrossVariable(machine,"PRODUCTION", date);

//        returns Total-Scrap in the last day for machine
        int ttlScrap = productionService.calculateGrossVariable(machine, "SCRAP", date);

//        calculation of PERFORMANCE, AVAILABILITY, QUALITY  & OEE as instructed in Assignment C
//        **** Value of Availability is accurate here as in your resource endpoint for Assignment C you
//        divided by 18 hours rather than 16 ***
        float performance = (float) ttlGrossProduction / (30000*24);
        float availability = (float) (24 * 60 - downtime) / (16 * 60);
        float quality = (float) (ttlGrossProduction - ttlScrap)/ttlGrossProduction;
        float oee = performance * availability * quality;

        LOG.log(Level.INFO, "ttl scrap is " + ttlScrap);

//        adding values to model for Thymeleaf - Assignment C
        model.addAttribute("performance", performance);
        model.addAttribute("availability", availability );
        model.addAttribute("quality", quality);
        model.addAttribute("oee", oee);
        return "machine";
    }
}
