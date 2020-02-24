package model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode
public class Entity {

    private String symbol_token;

    private long time;
    private int open;
    private int low;
    private int high;
    private int close;

    public String getSymbol() {
        if (this.symbol_token != null)
            return this.symbol_token.split("__")[0];
        else return null;
    }

    public int getToken() {
        if (this.symbol_token != null)
            try {
                return Integer.parseInt(this.symbol_token.split("__")[1]);
            }catch (NumberFormatException e){
                return 0;
            }
        else return 0;
    }


}
