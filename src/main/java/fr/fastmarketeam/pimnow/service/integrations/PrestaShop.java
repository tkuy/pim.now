package fr.fastmarketeam.pimnow.service.integrations;

import fr.fastmarketeam.pimnow.service.impl.FileUploadServiceImpl;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URL;

public class PrestaShop {
    private final static Logger log = LoggerFactory.getLogger(PrestaShop.class);


    private final CloseableHttpClient httpclient ;
    private final String url ;
    private final boolean debug ;

    public PrestaShop(String url, String key, boolean debug) {

        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
            AuthScope.ANY,
            new UsernamePasswordCredentials(key, ""));

        this.url = url ;

        this.httpclient = HttpClients.custom()
            .setDefaultCredentialsProvider(credsProvider)
            .build();

        this.debug = debug ;
    }

    public int createProduct(String nameProduct, String descriptionProduct, String price, String quantity) throws IOException, ParserConfigurationException {

        /*
        Add the product to the prestashop
         */

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><prestashop xmlns:xlink=\"http://www.w3.org/1999/xlink\"><product><id/><id_manufacturer/><id_supplier/><id_category_default>2</id_category_default><new/><cache_default_attribute/><id_default_image/><id_default_combination/><id_tax_rules_group/><position_in_category/><type/><id_shop_default/><reference/><supplier_reference/><location/><width/><height/><depth/><weight/><quantity_discount/><ean13/><isbn/><upc/><cache_is_pack/><cache_has_attachments/><is_virtual/><state/><additional_delivery_times/><delivery_in_stock><language id=\"1\"/></delivery_in_stock><delivery_out_stock><language id=\"1\"/></delivery_out_stock><on_sale/><online_only/><ecotax/><minimal_quantity/><low_stock_threshold/><low_stock_alert/><price>" + price + "</price><wholesale_price/><unity/><unit_price_ratio/><additional_shipping_cost/><customizable/><text_fields/><uploadable_files/><active>1</active><redirect_type/><id_type_redirected/><available_for_order>1</available_for_order><available_date/><show_condition/><condition/><show_price>1</show_price><indexed>1</indexed><visibility/><advanced_stock_management/><date_add/><date_upd/><pack_stock_type/><meta_description><language id=\"1\" xlink:href=\"\">" + descriptionProduct + "</language></meta_description><meta_keywords><language id=\"1\" xlink:href=\""+this.url+"/api/languages/1\"><![CDATA[" + nameProduct + "]]></language></meta_keywords><meta_title><language id=\"1\" xlink:href=\""+this.url+"/api/languages/1\"><![CDATA["+nameProduct+"]]></language></meta_title><link_rewrite><language id=\"1\" xlink:href=\""+this.url+"/api/languages/1\"><![CDATA[" + nameProduct + "]]></language></link_rewrite><name><language id=\"1\" xlink:href=\""+this.url+"/api/languages/1\"><![CDATA[" + nameProduct + "]]></language></name><description><language id=\"1\" xlink:href=\""+this.url+"/api/languages/1\"><![CDATA[" + descriptionProduct + "]]></language></description><description_short><language id=\"1\" xlink:href=\""+this.url+"/api/languages/1\"><![CDATA["+nameProduct+"]]></language></description_short><available_now><language id=\"1\" xlink:href=\""+this.url+"/api/languages/1\"><![CDATA[" + nameProduct + "]]></language></available_now><available_later><language id=\"1\" xlink:href=\""+this.url+"/api/languages/1\"><![CDATA[available later]]></language></available_later><associations><categories><category><id/></category><category><id>2</id></category></categories><images><image><id/></image></images><combinations><combination><id/></combination></combinations><product_option_values><product_option_value><id/></product_option_value></product_option_values><product_features><product_feature><id/><id_feature_value/></product_feature></product_features><tags><tag><id/></tag></tags><stock_availables><stock_available><id/><id_product_attribute/></stock_available></stock_availables><accessories><product><id/></product></accessories><product_bundle><product><id/><quantity/></product></product_bundle></associations></product></prestashop>" ;
        StringEntity entity = new StringEntity(xml, ContentType.create("text/xml", Consts.UTF_8));
        HttpPost httppost = new HttpPost(this.url + "/api/products?schema=blank");
        httppost.setEntity(entity);

        String response = null ;

        try {
            response = executeRequest(httppost) ;

            if(debug)
                System.out.println(response) ;

            if(response.contains("error")) {
                System.out.println("An error occured into prestashop module. Failed to add product into ps. Maybe the product is wrong or some data are not good.\r\n" + response) ;
                return 0 ;
            }

        }   catch(IOException e) {
            System.out.println("An error occured into prestashop module. Failed to insert quantity into ps.");
        }

        /*
        Recover the id of the product and the id of the stock
         */

        Document document = convertStringToXMLDocument(response);
        Element e = document.getDocumentElement();
        if (e == null) throw new IllegalStateException("The element of this document is null");
        e.normalize();
        int productId = Integer.parseInt(document.getElementsByTagName("id").item(0).getFirstChild().getNodeValue()) ;
        int idCount = document.getElementsByTagName("id").getLength() ;
        int stock_availables_id = Integer.parseInt(document.getElementsByTagName("id").item(idCount - 1).getFirstChild().getNodeValue()) ;

        if(debug)
            System.out.println("id_product : " + productId + " | stock_availables_id : " + stock_availables_id) ;

        /*
        Add the quantity of a product
         */

        String xmlQuantity = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><prestashop xmlns:xlink=\"http://www.w3.org/1999/xlink\"><stock_available><id><![CDATA["+stock_availables_id+"]]></id><id_product xlink:href=\""+this.url+"/api/products/"+productId+"\">" + productId + "</id_product><id_product_attribute>0</id_product_attribute><id_shop xlink:href=\"http://benjamindinh.fr/prestashop/api/shops/1\">1</id_shop><id_shop_group>0</id_shop_group><quantity>"+quantity+"</quantity><depends_on_stock>0</depends_on_stock><out_of_stock>2</out_of_stock><location/></stock_available></prestashop>" ;
        entity = new StringEntity(xmlQuantity, ContentType.create("text/xml", Consts.UTF_8));
        HttpPut httpput = new HttpPut(this.url + "/api/stock_availables/"+stock_availables_id);
        httpput.setEntity(entity);

        try {
            response = executeRequest(httpput) ;

            if(debug)
                System.out.println(response) ;

            if(response.contains("error")) {
                System.out.println("An error occured into prestashop module. Failed to add quantity product into ps. Maybe the product id is wrong.\r\n" + response) ;
                return 0 ;
            }

        }   catch(IOException err) {
            System.out.println("An error occured into prestashop module. Failed to insert quantity into ps.");
        }

        return productId ;
    }

    public boolean editProduct(int productId, String nameProduct, String descriptionProduct, String price) throws IOException {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><prestashop xmlns:xlink=\"http://www.w3.org/1999/xlink\"><product><id>"+productId+"</id><id_manufacturer/><id_supplier/><id_category_default>2</id_category_default><new/><cache_default_attribute/><id_default_image/><id_default_combination/><id_tax_rules_group/><position_in_category/><type/><id_shop_default/><reference/><supplier_reference/><location/><width/><height/><depth/><weight/><quantity_discount/><ean13/><isbn/><upc/><cache_is_pack/><cache_has_attachments/><is_virtual/><state/><additional_delivery_times/><delivery_in_stock><language id=\"1\"/></delivery_in_stock><delivery_out_stock><language id=\"1\"/></delivery_out_stock><on_sale/><online_only/><ecotax/><minimal_quantity/><low_stock_threshold/><low_stock_alert/><price>" + price + "</price><wholesale_price/><unity/><unit_price_ratio/><additional_shipping_cost/><customizable/><text_fields/><uploadable_files/><active>1</active><redirect_type/><id_type_redirected/><available_for_order>1</available_for_order><available_date/><show_condition/><condition/><show_price>1</show_price><indexed>1</indexed><visibility/><advanced_stock_management/><date_add/><date_upd/><pack_stock_type/><meta_description><language id=\"1\" xlink:href=\"\">" + descriptionProduct + "</language></meta_description><meta_keywords><language id=\"1\" xlink:href=\""+this.url+"/api/languages/1\"><![CDATA[" + nameProduct + "]]></language></meta_keywords><meta_title><language id=\"1\" xlink:href=\""+this.url+"/api/languages/1\"><![CDATA["+nameProduct+"]]></language></meta_title><link_rewrite/><name><language id=\"1\" xlink:href=\""+this.url+"/api/languages/1\"><![CDATA[" + nameProduct + "]]></language></name><description><language id=\"1\" xlink:href=\""+this.url+"/api/languages/1\"><![CDATA[" + descriptionProduct + "]]></language></description><description_short><language id=\"1\" xlink:href=\""+this.url+"/api/languages/1\"><![CDATA["+nameProduct+"]]></language></description_short><available_now><language id=\"1\" xlink:href=\""+this.url+"/api/languages/1\"><![CDATA[" + nameProduct + "]]></language></available_now><available_later><language id=\"1\" xlink:href=\""+this.url+"/api/languages/1\"><![CDATA[available later]]></language></available_later><associations><categories><category><id/></category><category><id>2</id></category></categories><images><image><id/></image></images><combinations><combination><id/></combination></combinations><product_option_values><product_option_value><id/></product_option_value></product_option_values><product_features><product_feature><id/><id_feature_value/></product_feature></product_features><tags><tag><id/></tag></tags><stock_availables><stock_available><id/><id_product_attribute/></stock_available></stock_availables><accessories><product><id/></product></accessories><product_bundle><product><id/><quantity/></product></product_bundle></associations></product></prestashop>" ;
        StringEntity entity = new StringEntity(xml, ContentType.create("text/xml", Consts.UTF_8));
        HttpPut httpput = new HttpPut(this.url + "/api/products/"+productId);
        httpput.setEntity(entity);

        try {
            String response = executeRequest(httpput) ;

            if(debug)
                System.out.println(response) ;

            if(response.contains("error")) {
                System.out.println("An error occured into prestashop module. Failed to edit product into ps. Maybe the product is already deleted.\r\n" + response) ;
                return false ;
            }


        }   catch(IOException e) {
            System.out.println("An error occured into prestashop module. Failed to edit product into ps.");
        }

        return true ;
    }

    public boolean deleteProduct(int productId) {
        HttpDelete httpdelete = new HttpDelete(this.url + "/api/products/"+productId);

        try {
            String response = executeRequest(httpdelete) ;

            if(debug)
                System.out.println(response) ;

            if(response.contains("error")) {
                System.out.println("An error occured into prestashop module. Failed to delete product from ps. Maybe the product is already deleted.\r\n" + response) ;
                return false ;
            }

        }   catch(IOException e) {
            System.out.println("An error occured into prestashop module. Failed to delete product into ps.");
        }

        return true ;

    }

    public boolean addImage(String urlImg, int productId) throws IOException {

        URL url = new URL(urlImg);
        InputStream is = url.openStream();

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        is.close();
        String completeUrl =  this.url + "/api/images/products/" + productId;
        HttpPost httppost = new HttpPost(completeUrl);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addPart("image", new ByteArrayBody(buffer.toByteArray(), "upload.jpg"));

        HttpEntity entity = builder.build();
        httppost.setEntity(entity);

        try {
            String response = executeRequest(httppost); ;

            if(debug)
                System.out.println(response) ;

            if(response.contains("error")) {
                System.out.println("An error occured into prestashop module. Failed to add product image into ps. Maybe the product id is wrong.\r\n" + response) ;
                return false ;
            }

        }   catch(IOException e) {
            System.out.println("An error occured into prestashop module. Failed to delete product into ps.");
        }

        return true ;

    }



    protected String executeRequest(HttpUriRequest request) throws IOException {
        InputStreamReader isReader = new InputStreamReader(httpclient.execute(request).getEntity().getContent());

        BufferedReader reader = new BufferedReader(isReader);
        StringBuffer sb = new StringBuffer();
        String str;
        while((str = reader.readLine())!= null){
            sb.append(str);
        }

        return sb.toString() ;
    }

    private static Document convertStringToXMLDocument(String xmlString)
    {
        //Parser that produces DOM object trees from XML content
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        //API to obtain DOM Document instance
        DocumentBuilder builder = null;
        try
        {
            //Create DocumentBuilder with default configuration
            builder = factory.newDocumentBuilder();

            //Parse the content to Document object
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
            return doc;
        }
        catch (Exception e)
        {
            log.error(e.getMessage());
            return null;
        }
    }
}
