package com.bsep_sbz.SIEMCenter.model.sbz.rule;

import com.bsep_sbz.SIEMCenter.model.sbz.enums.rule.RelationalOperatorType;

public class Occurrence
{
    private int count;
    private RelationalOperatorType operator;

    public Occurrence(int count, RelationalOperatorType operator) {
        this.count = count;
        this.operator = operator;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public RelationalOperatorType getOperator() {
        return operator;
    }

    public void setOperator(RelationalOperatorType operator) {
        this.operator = operator;
    }
}
