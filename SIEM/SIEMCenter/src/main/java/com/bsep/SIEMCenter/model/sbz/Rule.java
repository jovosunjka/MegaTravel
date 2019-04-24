package com.bsep.SIEMCenter.model.sbz;




import com.bsep.SIEMCenter.model.sbz.enums.LogicalOperator;

import java.util.List;


public class Rule {
    private String name;
    private List<Condition> conditions;
    private List<LogicalOperator> operators;

    public Rule() {

    }

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
