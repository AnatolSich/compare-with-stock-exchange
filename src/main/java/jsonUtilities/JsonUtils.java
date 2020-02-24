package jsonUtilities;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import lombok.RequiredArgsConstructor;
import model.Entity;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import service.DateTimeService;
import service.MatchService;
import service.Types;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RequiredArgsConstructor
public class JsonUtils {

    private static final Logger log = Logger.getLogger(JsonUtils.class);

    private final Properties appProps;
    private final MatchService matchService;
    private final DateTimeService dateTimeService;

    public Map<String, List<Entity>> parseFromNiftyToMap(JSONObject jsonObject) {
        JSONArray jsonArray = (JSONArray) jsonObject.get("data");
        long time = getNiftyNanoTime(jsonObject);
        Map<String, List<Entity>> map = new HashMap<>();

        for (Object object : jsonArray) {
            JSONObject jsonObjectTemp = (JSONObject) object;
            Entity entity = this.parseJsonToEntity(jsonObjectTemp, time);
            List<Entity> list = new ArrayList<>();
            list.add(entity);
            map.put(entity.getSymbol_token(), list);
        }
        return map;
    }

    public long getNiftyNanoTime(JSONObject jsonObject) {
        String formatedTime = jsonObject.get("time").toString();
        LocalDateTime localDateTime = LocalDateTime.parse(formatedTime, DateTimeFormatter.ofPattern(appProps.getProperty("upstox.compare.nifty-time-format")));
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("Asia/Kolkata"));
        log.info("zonedDateTime = " + zonedDateTime.toString());
        return zonedDateTime.toInstant().toEpochMilli();
    }

    private Entity parseJsonToEntity(JSONObject jsonObject, long time) {
        String token = matchService.matchTokenBySymbol(Types.NSE, jsonObject.get("symbol").toString());
        return Entity.builder()
                .symbol_token(jsonObject.get("symbol").toString() + "__" + token)
                .time(time)
                .open((int) (Double.parseDouble(checkJsonParams(jsonObject.get("open"))) * 100))
                .low((int) (Double.parseDouble(checkJsonParams(jsonObject.get("low"))) * 100))
                .high((int) (Double.parseDouble(checkJsonParams(jsonObject.get("high"))) * 100))
                .close((int) (Double.parseDouble(checkJsonParams(jsonObject.get("lrP"))) * 100))
                .build();
    }

    private String checkJsonParams(Object object) {
        if (object == null || object.toString().isBlank()) return "0";
        return object.toString().replaceAll(",", "");
    }

    public JSONObject parseStringToJson() {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = (JSONObject) parser.parse(getJsonFromFile());
        } catch (ParseException e) {
            log.error(e.getMessage());
        }
        return jsonObject;
    }

    public String getJsonFromFile() {
        String logPath = appProps.getProperty("upstox.compare.NSE.location");
        List<String> lines = null;
        try {
            //noinspection UnstableApiUsage
            lines = Files.readLines(new File(logPath), Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String result = lines != null ? lines.get(14) : null;
        if (result != null && result.startsWith("{\"declines\":"))
            return result;
        else return null;
    }
}
