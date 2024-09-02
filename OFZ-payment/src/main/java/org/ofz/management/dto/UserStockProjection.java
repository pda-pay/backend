package org.ofz.management.dto;

public interface UserStockProjection {
    public String getAccountNumber();
    public int getQuantity();
    public int getMortgagedQuantity();

    public String getStockCode();
    public String getCompanyCode();
}
