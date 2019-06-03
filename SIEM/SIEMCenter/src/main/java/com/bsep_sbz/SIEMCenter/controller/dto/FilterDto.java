package com.bsep_sbz.SIEMCenter.controller.dto;

public class FilterDto
{
    private String regExp;
    private String column;

    public String getRegExp() {
        return regExp;
    }

    public void setRegExp(String regExp) {
        this.regExp = regExp;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }
}
