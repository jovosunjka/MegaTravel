package com.bsep.SiemCenterRules.model;


import com.bsep.SiemCenterRules.model.enums.RelationalOperator;

import java.util.List;

public class Rule {
    private String name;
    private List<Condition> conditions;
    private List<RelationalOperator> relationalOperators;

    public Rule() {

    }

    public Rule(String name, List<Condition> conditions, List<RelationalOperator> relationalOperators) {
        this.name = name;
        this.conditions = conditions;
        this.relationalOperators = relationalOperators;
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

    public List<RelationalOperator> getRelationalOperators() {
        return relationalOperators;
    }

    public void setRelationalOperators(List<RelationalOperator> relationalOperators) {
        this.relationalOperators = relationalOperators;
    }
}
