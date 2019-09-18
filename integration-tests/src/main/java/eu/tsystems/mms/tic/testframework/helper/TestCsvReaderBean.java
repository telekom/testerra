/*
 * Created on 18.09.2019
 */
package eu.tsystems.mms.tic.testframework.helper;

/**
 * TestCSVReader1
 * <p>
 * Date: 18.09.2019
 * Time: 09:21
 *
 * @author Eric.Kubenka <Eric.Kubenka@t-systems.com>
 */
public class TestCsvReaderBean {

    public String id;
    public String serviceNumber;
    public String price;
    public String productName;
    public String date;
    public String calculate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceNumber() {
        return serviceNumber;
    }

    public void setServiceNumber(String serviceNumber) {
        this.serviceNumber = serviceNumber;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCalculate() {
        return calculate;
    }

    public void setCalculate(String calculate) {
        this.calculate = calculate;
    }
}
