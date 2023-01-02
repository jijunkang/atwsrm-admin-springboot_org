package org.springblade.common.utils;

import java.math.BigDecimal;

public
class WillU9Util{
    public static
    String getTaxRateCode( BigDecimal taxRate){
        if(taxRate == null){
            return "";
        }
        if(taxRate.compareTo(BigDecimal.ZERO) == 0){
            return "TS03";
        }
        if(taxRate.compareTo(new BigDecimal("0.01")) == 0){
            return "TS16";
        }
        if(taxRate.compareTo(new BigDecimal("0.03")) == 0){
            return "TS06";
        }
        if(taxRate.compareTo(new BigDecimal("0.05")) == 0){
            return "TS13";
        }
        if(taxRate.compareTo(new BigDecimal("0.06")) == 0){
            return "TS07";
        }
        if(taxRate.compareTo(new BigDecimal("0.09")) == 0){
            return "TS15";
        }
        if(taxRate.compareTo(new BigDecimal("0.1")) == 0){
            return "TS12";
        }
        if(taxRate.compareTo(new BigDecimal("0.12")) == 0){
            return "TS09";
        }
        if(taxRate.compareTo(new BigDecimal("0.13")) == 0){
            return "TS08";
        }
        if(taxRate.compareTo(new BigDecimal("0.16")) == 0){
            return "TS10";
        }
        if(taxRate.compareTo(new BigDecimal("0.17")) == 0){
            return "TS01";
        }

        return "";
    }
}
