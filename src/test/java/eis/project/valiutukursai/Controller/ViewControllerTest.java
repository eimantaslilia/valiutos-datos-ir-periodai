package eis.project.valiutukursai.Controller;

import eis.project.valiutukursai.Entity.Rate;
import eis.project.valiutukursai.Service.XMLService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ViewController.class)
class ViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private XMLService xmlService;

    @Test
    void getEurRatesByDateGET() throws Exception{

        mockMvc.perform(get("/valiutu-kursai/data"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("currencyList"))
                .andExpect(view().name("eurRatesByDate"));

        verify(xmlService, only()).listOfAvailableCurrencies(anyString());
    }

    @Test
    void getEurRatesByDatePOST() throws Exception{

        mockMvc.perform(post("/valiutu-kursai/data"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("currencyList"))
                .andExpect(model().attributeExists("rates"))
                .andExpect(model().attributeExists("date"))
                .andExpect(view().name("eurRatesByDate"));

        verify(xmlService, times(1)).listOfRates(anyString());
        verify(xmlService, times(1)).listOfAvailableCurrencies(anyString());
    }

    @Test
    void getCurrencyRatesByPeriodGET() throws Exception{

        mockMvc.perform(get("/valiutu-kursai/periodas"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("currencyList"))
                .andExpect(view().name("currencyRatesByPeriodAndCode"));

        verify(xmlService, only()).listOfAvailableCurrencies(anyString());
    }

    @Test
    @DisplayName("Period + Code POST = first date is earlier than second date")
    void getCurrencyRatesByPeriodPOSTBadDate() throws Exception{
        mockMvc.perform(post("/valiutu-kursai/periodas")
                .flashAttr("dateFrom", "2019-05-05")
                .flashAttr("dateTo", "2019-01-01"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("dateError"))
         .andExpect(view().name("redirect:/valiutu-kursai/periodas"));

        verifyNoInteractions(xmlService);
    }

    @Test
    @DisplayName("Period + Code POST = currency code was not selected")
    void getCurrencyRatesByPeriodPOSTNoCode() throws Exception{
        mockMvc.perform(post("/valiutu-kursai/periodas")
                .flashAttr("currency", "unselected")
                .flashAttr("dateFrom", "2019-05-05")
                .flashAttr("dateTo", "2019-10-10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("unselected"))
                .andExpect(view().name("redirect:/valiutu-kursai/periodas"));

        verifyNoInteractions(xmlService);
    }
    @Test
    @DisplayName("Period + Code POST = valid dates and valid code")



    void getCurrencyRatesByPeriodPOSTValidDateValidCode() throws Exception{

        Rate rate1 = new Rate("2019-05-02", "AUD", 10.7);
        Rate rate2 = new Rate("2019-04-02", "AUD", 9.7);
        List<Rate> rates = new ArrayList<>(Arrays.asList(rate1, rate2));
        given(xmlService.listOfRates(anyString())).willReturn(rates);

        mockMvc.perform(post("/valiutu-kursai/periodas")
                .flashAttr("currency", "GBP")
                .flashAttr("dateFrom", "2019-05-05")
                .flashAttr("dateTo", "2019-10-10"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("firstDate"))
                .andExpect(model().attributeExists("lastDate"))
                .andExpect(model().attributeExists("currencyCode"))
                .andExpect(model().attributeExists("currencyList"))
                .andExpect(view().name("currencyRatesByPeriodAndCode"));

        verify(xmlService, times(1)).listOfRates(anyString());
        verify(xmlService, times(1)).listOfAvailableCurrencies(anyString());
    }
}