package com.homearound;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.Property;

public class GeneratorClass {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "com.homearound.www.homearound.db");
        Entity customerjob = schema.addEntity("CustomerJob");

        customerjob.addIdProperty().primaryKey().autoincrement();
        customerjob.addStringProperty("jobcategory");
        customerjob.addDoubleProperty("jobcost");
        customerjob.addStringProperty("jobdetail");
        customerjob.addStringProperty("jobstatus");
        customerjob.addStringProperty("jobtitle");
        customerjob.addStringProperty("timecreated");
        customerjob.addStringProperty("timefinish");

        Entity customerservice = schema.addEntity("CustomerService");

        customerservice.addIdProperty().primaryKey().autoincrement();
        customerservice.addStringProperty("category");
        customerservice.addStringProperty("detail");
        customerservice.addStringProperty("distance");
        customerservice.addStringProperty("email");
        customerservice.addStringProperty("name");

        Entity customermessage = schema.addEntity("CustomerMessage");

        customermessage.addIdProperty().primaryKey().autoincrement();
        customermessage.addStringProperty("direction");
        customermessage.addStringProperty("email");
        customermessage.addStringProperty("messagebody");
        customermessage.addStringProperty("messagetime");
        customermessage.addStringProperty("name");

        Entity customermessagebox = schema.addEntity("CustomerMessageBox");

        customermessagebox.addIdProperty().primaryKey().autoincrement();
        customermessagebox.addStringProperty("email");
        customermessagebox.addStringProperty("lastmessage");
        customermessagebox.addStringProperty("name");
        customermessagebox.addStringProperty("timelastmessage");

        Entity merchantjob = schema.addEntity("MerchantJob");

        merchantjob.addIdProperty().primaryKey().autoincrement();
        merchantjob.addStringProperty("distance");
        merchantjob.addDoubleProperty("email");
        merchantjob.addStringProperty("jobdetail");
        merchantjob.addStringProperty("jobstatus");
        merchantjob.addStringProperty("jobtitle");
        merchantjob.addStringProperty("name");
        merchantjob.addStringProperty("timefinish");
        merchantjob.addStringProperty("timepost");

        Entity merchantmessage = schema.addEntity("MerchantMessage");

        merchantmessage.addIdProperty().primaryKey().autoincrement();
        merchantmessage.addStringProperty("direction");
        merchantmessage.addStringProperty("email");
        merchantmessage.addStringProperty("messagebody");
        merchantmessage.addStringProperty("messagetime");
        merchantmessage.addStringProperty("name");

        Entity merchantmessagebox = schema.addEntity("MerchantMessageBox");

        merchantmessagebox.addIdProperty().primaryKey().autoincrement();
        merchantmessagebox.addStringProperty("email");
        merchantmessagebox.addStringProperty("lastmessage");
        merchantmessagebox.addStringProperty("name");
        merchantmessagebox.addStringProperty("timelastmessage");

        new DaoGenerator().generateAll(schema, "./app/src/main/java");
    }
}
