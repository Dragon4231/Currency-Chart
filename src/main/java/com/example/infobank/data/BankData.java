package com.example.infobank.data;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Calendar;

@Data
public class BankData {
    String currency;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Calendar startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Calendar endDate;
}
