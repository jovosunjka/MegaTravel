package com.bsep_sbz.SIEMCenter.model.sbz.rule;

import com.bsep_sbz.SIEMCenter.model.sbz.enums.log.LogicalOperator;

import java.util.List;


public class Rule {
    private String name;
    private List<Condition> conditions;
    // number of operators = number of conditions - 1
    // operators are placed between each two conditions respectively
    // example: c1 o1 c2 o2 c3
    private List<LogicalOperator> operators;

    public Rule(String name, List<Condition> conditions, List<LogicalOperator> operators) {
        this.name = name;
        this.conditions = conditions;
        this.operators = operators;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

    public List<LogicalOperator> getOperators() {
        return operators;
    }

    public void setOperators(List<LogicalOperator> operators) {
        this.operators = operators;
    }
}
