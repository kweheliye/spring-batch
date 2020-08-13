package com.mashreq.wealth.helper;

import com.mashreq.wealth.client.request.CustomerSearchRequest;
import com.mashreq.wealth.enums.BusinessGroupType;
import com.mashreq.wealth.model.CustomerInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Slf4j
public class CSVReaderTest {

    private CSVReader csvReader;
    private Path resourceDirectory = null;

    @BeforeEach
    public void initOnlyOnce() {
        csvReader = new CSVReader();
        //the file being test it, it's under the test/resource folder
        resourceDirectory = Paths.get("src", "test", "resources", "uae_MACIS.txt");
    }

    @Test
    public void readCustomerInfoFromCSV_whenFileExistAndHasRealData_thenReturnListOfCustomers() {
        String filePath = resourceDirectory.toFile().getAbsolutePath();
        Map<String, CustomerInfo> result = csvReader.readCustomerInfoFromCSV(filePath);
        Assert.assertFalse(result.isEmpty());
        Assert.assertTrue(result.size() > 1);
    }

    @Test
    public void readCustomerInfoFromCSV_whenFileNotExist_thenReturnException() {
        String filePath = resourceDirectory.toFile().getAbsolutePath();
        Map<String, CustomerInfo> result = csvReader.readCustomerInfoFromCSV("wrongfilePath");
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void getCustomerInfoMapSize() {
        String filePath = resourceDirectory.toFile().getAbsolutePath();
        Map<String, CustomerInfo> result = csvReader.readCustomerInfoFromCSV(filePath);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(csvReader.getCustomerInfoMap().size(), result.size());
    }

    @Test
    public void mapToCustomerInfoDto_WithValidDataLines_ReturnCustomerInfoObject() {
        String line= "013368406|MANSOUR ELSAYED YOUSS MOHAMED|I|MANSOUR ELSAYED YOU||M784197646186052|18062021|M|S|19760521|ENG|99|18|||7500.000|||||20200706||20200705|0|N|A|1|20200705|1|0.0|0.0|||||||||N||7|||0|0|0|||||1.|MOBANKUSER|0||||0||006|N||5.||||0||0|N|Y|||||||971501557970|0||||0|4|0|EG||N|0|| |0|0| |00010101|kamalhashi@hotmail.com|N||||AE|N|Y||DIGIBANK|784197646186052**||||tatweerpest@gmail.com|IND0021||||||||||RBG|CSS0048|SBR0006|AE|||||||13368406|||||||R||784197646186052|||||||DIGIBANK|S";
        CustomerInfo result = csvReader.mapToCustomerInfoDto.apply(line);
        CustomerInfo expected= CustomerInfo.builder().cifId("013368406").email("kamalhashi@hotmail.com").businessGroupType(BusinessGroupType.RBG).build();
        Assert.assertEquals(expected, result);
    }

    @Test
    public void mapToCustomerInfoDto_withInvalidDataLines_ReturnNullCustomerInfoObject() {
        String line= "0133684|MANSOUR ELSAYED YOUSS MOHAMED|I|MANSOUR ELSAYED YOU||M784197646186052|18062021|M|S|19760521|ENG|99|18|||7500.000|||||20200706||20200705|0|N|A|1|20200705|1|0.0|0.0|||||||||N||7|||0|0|0|||||1.|MOBANKUSER|0||||0||006|N||5.||||0||0|N|Y|||||||971501557970|0||||0|4|0|EG||N|0|| |0|0| |00010101|kamalhashi@hotmail.com|N||||AE|N|Y||DIGIBANK|784197646186052**||||tatweerpest@gmail.com|IND0021||||||||||RBG|CSS0048|SBR0006|AE|||||||13368406|||||||R||784197646186052|||||||DIGIBANK|S";
        CustomerInfo result = csvReader.mapToCustomerInfoDto.apply(line);
        CustomerInfo expected= null;
        Assert.assertEquals(expected, result);
    }

}