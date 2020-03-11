import fr.fastmarketeam.pimnow.service.integrations.PrestaShop;

import java.io.IOException;

public class PrestashopTest {
    public static void main(String[] args) throws IOException {
        PrestaShop prestaShop = new PrestaShop("http://benjamindinh.fr/prestashop", "BPUYE9EA2NWR286APFJ66F4SYMY8GE3X", true);
        prestaShop.addImage("https://pimnow.benjamindinh.fr/files/2020-03-01-11:28:59-12085bc7da99d11ae115863e23bd_chicken1.jpg", 334);
    }
}
