package eis.project.valiutukursai.Controller;

import eis.project.valiutukursai.Entity.Rate;
import eis.project.valiutukursai.Service.XMLService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/valiutu-kursai")
public class ViewController {

    private static final String currencyCodesURL = "http://lb.lt/webservices/fxrates/FxRates.asmx/getFxRates?tp=EU&dt=";

    private final Logger logger = LoggerFactory.getLogger(ViewController.class);

    @Autowired
    private XMLService xmlService;

    @GetMapping("/data")
    public ModelAndView getEurRatesByDateGET() throws Exception {

        ModelAndView mav = new ModelAndView("eurRatesByDate");
        mav.addObject("currencyList", xmlService.listOfAvailableCurrencies(currencyCodesURL + currentDate()));
        return mav;
    }

    @PostMapping("/data")
    public ModelAndView getEurRatesByDatePOST(@ModelAttribute("date") String date,
                                              @ModelAttribute("currency") String currency,
                                              RedirectAttributes ra) throws Exception {

        String URL = currencyCodesURL + date;
        List<Rate> rates = xmlService.listOfRates(URL);

        Rate chosenCurrencyRate = new Rate();
        if (!currency.equals("None")) {
            for (Rate rate : rates) {
                if (rate.getCurrencyTypeAbbr().equals(currency)) {
                    chosenCurrencyRate = rate;
                    break;
                }
            }
            rates.clear();
            rates.add(chosenCurrencyRate);
        }

        ModelAndView mav = new ModelAndView("eurRatesByDate");
        mav.addObject("currencyList", xmlService.listOfAvailableCurrencies(currencyCodesURL + currentDate()));
        mav.addObject("rates", rates);
        ra.addFlashAttribute("date", date);
        return mav;
    }

    @GetMapping("/periodas")
    public ModelAndView getCurrencyRatesByPeriodGET() throws Exception {

        ModelAndView mav = new ModelAndView("currencyRatesByPeriodAndCode");
        mav.addObject("currencyList", xmlService.listOfAvailableCurrencies(currencyCodesURL + currentDate()));
        return mav;
    }

    @PostMapping("/periodas")
    public ModelAndView getCurrencyRatesByPeriodPOST(@ModelAttribute("currency") String currencyCode,
                                                     @ModelAttribute("dateFrom") String dateFrom,
                                                     @ModelAttribute("dateTo") String dateTo,
                                                     RedirectAttributes ra) throws Exception {

        if (firstDateBeforeSecond(dateFrom, dateTo)) {
            ra.addFlashAttribute("dateError", "ši data turi būti prieš antrają datą");
            return new ModelAndView("redirect:/valiutu-kursai/periodas");
        }

        if (currencyCode.equals("unselected")) {
            ra.addFlashAttribute("unselected", "šis laukas yra būtinas");
            return new ModelAndView("redirect:/valiutu-kursai/periodas");
        }

        String URL = "http://lb.lt/webservices/fxrates/FxRates.asmx/getFxRatesForCurrency?tp=EU&ccy=" + currencyCode
                + "&dtFrom=" + dateFrom
                + "&dtTo=" + dateTo;
        List<Rate> eurRatesByPeriodList = xmlService.listOfRates(URL);

        ModelAndView mav = new ModelAndView("currencyRatesByPeriodAndCode");

        Double currencyChange = eurRatesByPeriodList.get(0).getAmount() - eurRatesByPeriodList.get(eurRatesByPeriodList.size() - 1).getAmount();
        if (currencyChange.equals(0.0)) {
            mav.addObject("didNotChange", "Nepasikeitė");
        } else {
            mav.addObject("currencyChange", currencyChange);
        }

        mav.addObject("firstDate", dateFrom);
        mav.addObject("lastDate", dateTo);
        mav.addObject("currencyCode", currencyCode);
        mav.addObject("currencyList", xmlService.listOfAvailableCurrencies(currencyCodesURL + currentDate()));
        return mav;
    }

    private boolean firstDateBeforeSecond(String from, String to) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date firstDate = format.parse(from);
        Date secondDate = format.parse(to);

        return secondDate.compareTo(firstDate) <= 0;
    }

    private String currentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }

}
