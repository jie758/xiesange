package com.xiesange.core.notify.mail;

import java.util.ArrayList;
import java.util.List;

public class TableTitleRow {
	private List<String> cellValues;
    private List<Integer> cellWidth;

    public TableTitleRow()
    {
        this.cellValues = new ArrayList<String>();
        this.cellWidth = new ArrayList<Integer>();
    }

    public TableTitleRow addCell(String value, int width)
    {
        cellValues.add(value);
        cellWidth.add(width);
        return this;
    }

    public List<String> getCellValues()
    {
        return cellValues;
    }

    public void setCellValues(List<String> cellValues)
    {
        this.cellValues = cellValues;
    }

    public List<Integer> getCellWidth()
    {
        return cellWidth;
    }

    public void setCellWidth(List<Integer> cellWidth)
    {
        this.cellWidth = cellWidth;
    }

    public String getCellValue(int i)
    {
        return cellValues.get(i);
    }

    public int getCellWidth(int i)
    {
        return cellWidth.get(i);
    }
}
