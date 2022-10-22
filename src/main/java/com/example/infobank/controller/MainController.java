package com.example.infobank.controller;

import com.example.infobank.data.BankData;
import com.example.infobank.services.ChartService;
import com.example.infobank.services.CurrencyService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/banks")
public class MainController {

    @Autowired
    CurrencyService currencyService;
    HashSet<String> currencies;

    public MainController(CurrencyService currencyService) throws JSONException, IOException {
        this.currencyService = currencyService;
        currencies = currencyService.setConnection();
    }

    @ModelAttribute(name = "bankInfo")
    public BankData order() {
        return new BankData();
    }

    @GetMapping
    public String showHomeForm(Model model) {
        model.addAttribute("listCurrency", currencies
                .stream().sorted()
                .collect(Collectors.toList()));
        return "banks";
    }

    @PostMapping
    public String getPicture(BankData bankData, Model model) {
        if (bankData.getCurrency() == null || bankData.getStartDate() == null || bankData.getEndDate() == null) {
            model.addAttribute("message", "Не все поля введены");
            return showHomeForm(model);
        }
        ChartService chartService = new ChartService();
        try {
            if (!chartService.setConnection(bankData)) {
                model.addAttribute("message", "По введённым данным ничего не найдено");
                return showHomeForm(model);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        model.addAttribute("forX", chartService.date);
        model.addAttribute("forY", chartService.rate);
        return "picture";
    }

}
