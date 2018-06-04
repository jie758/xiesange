package com.xiesange.core.notify.mail;

import java.util.ArrayList;
import java.util.List;

public class TableRecordRow {
	private List<String> cellValues;
    private List<String> cellColors;

    public TableRecordRow()
    {
        cellValues = new ArrayList<String>();
        cellColors = new ArrayList<String>();
    }

    public TableRecordRow addCell(Object value)
    {
        return this.addCell(value,null);
    }
    
    public TableRecordRow addCell(Object value,String color){
        if (value == null || value.toString().length() == 0)
            cellValues.add("&nbsp;");
        else
            cellValues.add(value.toString());
        cellColors.add(color);
        return this;
    }

    public List<String> getCellValues()
    {
        return cellValues;
    }

    public List<String> getCellColors()
    {
        return cellColors;
    }
}
