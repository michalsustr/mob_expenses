package sk.sustr.michal.mob_expense;

import java.util.Date;

/**
 * Created by Michal Sustr [michal.sustr@gmail.com] on 3/2/16.
 */
public class Item {
    public Date date;
    public String category;
    public Float amount;

    public Item(Date date, Float amount, String category) {
        this.amount = amount;
        this.category = category;
        this.date = date;
    }
}
