package com.sippy.wrapper.parent;

import com.sippy.wrapper.parent.database.DatabaseConnection;
import com.sippy.wrapper.parent.request.JavaTestRequest;
import com.sippy.wrapper.parent.response.JavaTestResponse;
import java.util.*;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sippy.wrapper.parent.database.dao.TnbDao;
import com.sippy.wrapper.parent.request.GetTnbListRequest;

@Stateless
public class WrappedMethods {

    private static final Logger LOGGER = LoggerFactory.getLogger(WrappedMethods.class);

    @EJB
    DatabaseConnection databaseConnection;

    @RpcMethod(name = "javaTest", description = "Check if everything works :)")
    public Map<String, Object> javaTest(JavaTestRequest request) {
        JavaTestResponse response = new JavaTestResponse();

        int count = databaseConnection.getAllTnbs().size();

        LOGGER.info("the count is: " + count);

        response.setId(request.getId());
        String tempFeeling = request.isTemperatureOver20Degree() ? "warm" : "cold";
        response.setOutput(
                String.format(
                        "%s has a rather %s day. And he has %d tnbs", request.getName(), tempFeeling, count));

        Map<String, Object> jsonResponse = new HashMap<>();
        jsonResponse.put("faultCode", "200");
        jsonResponse.put("faultString", "Method success");
        jsonResponse.put("something", response);

        return jsonResponse;
    }

    @RpcMethod(name = "getTnbList", description = "Fetch TNB list from the database")
    public Map<String, Object> getTnbList(GetTnbListRequest request) {
        LOGGER.info("Fetching TNB list from the database");

        Map<String, Object> jsonResponse = new HashMap<>();

        try {
            List<TnbDao> allTnbs = databaseConnection.getAllTnbs();
            TnbDao specificTnb = null;

            if (request.getNumber() != null) {
              specificTnb = databaseConnection.getTnbById(request.getNumber());
            }

            List<TnbDao> filteredTnbs = new ArrayList<>();

            TnbDao telekom = new TnbDao();
            telekom.setTnb("D001");
            telekom.setName("Deutsche Telekom");
            telekom.setIsTnb(specificTnb != null && "D001".equals(specificTnb.getTnb()));
            filteredTnbs.add(telekom);

            for (TnbDao tnb : allTnbs) {
                if (tnb.getTnb().matches("(D146|D218|D248)")) {
                    continue;
                } 

                TnbDao tnbDao = new TnbDao();
                tnbDao.setTnb(tnb.getTnb());
                tnbDao.setName(tnb.getName());
                tnbDao.setIsTnb(specificTnb != null && tnb.getTnb().equals(specificTnb.getTnb()));

                filteredTnbs.add(tnbDao);
            }

            filteredTnbs.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));

            jsonResponse.put("faultCode", "200");
            jsonResponse.put("faultString", "Method success");
            jsonResponse.put("tnbs", filteredTnbs);

        } catch (Exception e) {
            LOGGER.error("Error while fetching TNB list: ", e);
            jsonResponse.put("faultCode", "500");
        }

        return jsonResponse;
    }
}
