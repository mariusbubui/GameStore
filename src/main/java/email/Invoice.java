package email;

import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.spire.doc.Table;
import com.spire.doc.fields.Field;
import game.Game;
import order.Order;
import user.Customer;

/**
 *
 */
public class Invoice {
    /**
     *
     */
    private final static String pathLoad = "src/main/resources/invoice/invoice_template.docx";

    /**
     *
     */
    private final static String pathSave = "src/main/resources/invoice/invoice.pdf";


    /**
     *
     */
    private final Document invoice;

    /**
     *
     */
    private final Customer customer;

    /**
     *
     */
    private final Order order;

    /**
     *
     */
    public Invoice(Customer customer, Order order) {
        this.customer = customer;
        this.order = order;

        invoice = new Document();
        invoice.loadFromFile(pathLoad);

        writeData();

        invoice.isUpdateFields(true);

        invoice.saveToFile(pathSave, FileFormat.PDF);
    }

    /**
     *
     */
    private void writeData(){
        int rows = order.getOrder().size();
        Table table = invoice.getSections().get(0).getTables().get(1);

        invoice.replace("#InvoiceNum", Integer.toString(order.getId()), true, true);
        invoice.replace("#Name", customer.getLastName() + " " + customer.getFirstName(), true, true);
        invoice.replace("#Phone", customer.getPhoneNumber(), true, true);

        if(rows > 1)
            addRows(table, rows - 1);

        fillTableWithData(table);
    }

    /**
     *
     */
    private void addRows(Table table, int rows) {
        for (int i = 0; i < rows; i++) {
            table.getRows().insert(2 + i, table.getRows().get(1).deepClone());

            for (Object object : table.getRows().get(2 + i).getCells().get(3).getParagraphs().get(0).getChildObjects()) {
                if (object instanceof Field field) {
                    field.setCode(String.format("=B%d*C%d\\# \"0.0\"", 3 + i,3 + i));
                }
                break;
            }
        }
    }

    /**
     *
     */
    private void fillTableWithData(Table table) {
        int i = 0;

        for (Game key : order.getOrder().keySet()) {
            var row = table.getRows().get(++i).getCells();
            row.get(0).getParagraphs().get(0).setText(key.getName());
            row.get(1).getParagraphs().get(0).setText(String.valueOf(order.getOrder().get(key)));
            row.get(2).getParagraphs().get(0).setText(String.valueOf(key.getPrice()));
        }

        table.getRows().get(order.getOrder().size() + 2)
                .getCells().get(3).getParagraphs().get(0)
                .setText(String.valueOf(order.getTotalPrice()));


        double discount = customer.getNumberOfOrders() % 5 == 0 ? 0.05 : 0.0;

        table.getRows().get(order.getOrder().size() + 3)
                .getCells().get(3).getParagraphs().get(0)
                .setText(String.valueOf(order.getTotalPrice() * discount));

        table.getRows().get(order.getOrder().size() + 4)
                .getCells().get(3).getParagraphs().get(0)
                .setText(String.valueOf(order.getTotalPrice() - order.getTotalPrice() * discount));
    }

    /**
     *
     */
    public String getPath(){
        return pathSave;
    }
}
