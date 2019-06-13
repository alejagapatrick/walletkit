package com.breadwallet.corecrypto;

import com.google.common.primitives.UnsignedInteger;

import org.junit.Test;
import static org.junit.Assert.*;

public class AmountAIT {

    @Test
    public void testAmountCreateBtc() {
        Currency btc = Currency.create("Bitcoin", "Bitcoin", "btc", "native");
        Unit satoshi_btc = Unit.create(btc, "BTC-SAT", "Satoshi", "SAT");
        Unit btc_btc = Unit.create(btc, "BTC-BTC", "Bitcoin", "B", satoshi_btc, UnsignedInteger.valueOf(8));

        Amount btc1 = Amount.create(100000000, satoshi_btc).get();
        assertEquals(new Double(100000000), btc1.doubleAmount(satoshi_btc).get());
        assertEquals(new Double(1), btc1.doubleAmount(btc_btc).get());
        assertFalse(btc1.isNegative());

        Amount btc1n = btc1.negate();
        assertEquals(new Double(-100000000), btc1n.doubleAmount(satoshi_btc).get());
        assertEquals(new Double(-1), btc1n.doubleAmount(btc_btc).get());
        assertTrue(btc1n.isNegative());
        assertFalse(btc1n.negate().isNegative());

        Amount btc2 = Amount.create(1, btc_btc).get();
        assertEquals(new Double(1), btc1.doubleAmount(btc_btc).get());
        assertEquals(new Double(100000000), btc1.doubleAmount(satoshi_btc).get());

        assertEquals(btc1, btc2);

        Amount btc3 = Amount.create(1.5, btc_btc).get();
        assertEquals(new Double(1.5), btc3.doubleAmount(btc_btc).get());
        assertEquals(new Double(150000000), btc3.doubleAmount(satoshi_btc).get());

        Amount btc4 = Amount.create(-1.5, btc_btc).get();
        assertTrue(btc4.isNegative());
        assertEquals(new Double(-1.5), btc4.doubleAmount(btc_btc).get());
        assertEquals(new Double(-150000000), btc4.doubleAmount(satoshi_btc).get());

        assertEquals("-B1.50", btc4.toStringAsUnit(btc_btc, null).get());
        assertEquals("-SAT150,000,000", btc4.toStringAsUnit(satoshi_btc, null).get());
    }

    @Test
    public void testAmountCreateBtcString() {
        Currency btc = Currency.create("Bitcoin", "Bitcoin", "btc", "native");
        Unit satoshi_btc = Unit.create(btc, "BTC-SAT", "Satoshi", "SAT");
        Unit btc_btc = Unit.create(btc, "BTC-BTC", "Bitcoin", "B", satoshi_btc, UnsignedInteger.valueOf(8));

        Amount btc1s = Amount.create("100000000", false, satoshi_btc).get();
        assertEquals(new Double(100000000), btc1s.doubleAmount(satoshi_btc).get());
        assertEquals(new Double(1), btc1s.doubleAmount(btc_btc).get());

        Amount btc2s = Amount.create("1", false, btc_btc).get();
        assertEquals(new Double(100000000), btc2s.doubleAmount(satoshi_btc).get());
        assertEquals(new Double(1), btc2s.doubleAmount(btc_btc).get());

        assertEquals(btc1s, btc2s);

        Amount btc3s = Amount.create("0x5f5e100", false, satoshi_btc).get();
        assertEquals(new Double(100000000), btc3s.doubleAmount(satoshi_btc).get());
        assertEquals(new Double(1), btc3s.doubleAmount(btc_btc).get());
        assertEquals("SAT100,000,000", btc3s.toStringAsUnit(satoshi_btc, null).get());
        assertEquals("B1.00", btc3s.toStringAsUnit(btc_btc, null).get());

        Amount btc4s = Amount.create("0x5f5e100", true, satoshi_btc).get();
        assertEquals(new Double(-100000000), btc4s.doubleAmount(satoshi_btc).get());
        assertEquals(new Double(-1), btc4s.doubleAmount(btc_btc).get());
        assertEquals("-SAT100,000,000", btc4s.toStringAsUnit(satoshi_btc, null).get());
        assertEquals("-B1.00", btc4s.toStringAsUnit(btc_btc, null).get());

        assertFalse(Amount.create("w0x5f5e100", false, satoshi_btc).isPresent());
        assertFalse(Amount.create("0x5f5e100w", false, satoshi_btc).isPresent());
        assertFalse(Amount.create("1000000000000000000000000000000000000000000000000000000000000000000000000000000000000", false, satoshi_btc).isPresent());

        assertFalse(Amount.create("-1", false, satoshi_btc).isPresent());
        assertFalse(Amount.create("+1", false, satoshi_btc).isPresent());
        assertFalse(Amount.create("0.1", false, satoshi_btc).isPresent());
        assertFalse(Amount.create("1.1", false, satoshi_btc).isPresent());
        assertTrue(Amount.create("1.0", false, satoshi_btc).isPresent());
        assertTrue(Amount.create("1", false, satoshi_btc).isPresent());

        assertTrue(Amount.create("0.1", false, btc_btc).isPresent());
        assertTrue(Amount.create("1.1", false, btc_btc).isPresent());
        assertTrue(Amount.create("1.0", false, btc_btc).isPresent());
        assertTrue(Amount.create("1.", false, btc_btc).isPresent());

        assertEquals(new Double(10000000), Amount.create("0.1", false, btc_btc).get().doubleAmount(satoshi_btc).get());
        assertEquals(new Double(110000000), Amount.create("1.1", false, btc_btc).get().doubleAmount(satoshi_btc).get());
        assertEquals(new Double(100000000), Amount.create("1.0", false, btc_btc).get().doubleAmount(satoshi_btc).get());
        assertEquals(new Double(100000000), Amount.create("1.", false, btc_btc).get().doubleAmount(satoshi_btc).get());

        assertTrue(Amount.create("0.12345678", false, btc_btc).isPresent());
        assertFalse(Amount.create("0.123456789", false, btc_btc).isPresent());
    }

    @Test
    public void testAmountCreateEth() {
        Currency eth = Currency.create("Ethereum", "Ethereum", "eth", "native");

        Unit wei_eth = Unit.create(eth, "ETH-WEI", "WEI", "wei");
        Unit gwei_eth = Unit.create(eth, "ETH-GWEI", "GWEI",  "gwei", wei_eth, UnsignedInteger.valueOf(9));
        Unit ether_eth = Unit.create(eth, "ETH-ETH", "ETHER", "E",    wei_eth, UnsignedInteger.valueOf(18));

        Amount eth1 = Amount.create(1e9, gwei_eth).get();
        Amount eth2 = Amount.create(1.0, ether_eth).get();
        Amount eth3 = Amount.create(1.1, ether_eth).get();

        assertEquals(eth1, eth2);
        assertTrue(eth1.compareTo(eth3) < 0);
        assertNotEquals(eth1.compareTo(eth3), 0);
        assertEquals(eth1.add(eth1), eth2.add(eth2));

        assertEquals(new Double(0.0), eth2.sub(eth1).get().doubleAmount(wei_eth).get());
        assertEquals(new Double(0.0), eth2.sub(eth1).get().doubleAmount(ether_eth).get());
        assertEquals(new Double(2.0), eth2.add(eth1).get().doubleAmount(ether_eth).get());

        assertTrue(eth2.sub(eth3).get().isNegative());
        assertFalse(eth2.sub(eth2).get().isNegative());


        Amount a1 = Amount.create( "12.12345678", false, ether_eth).get();
        assertTrue(a1.doubleAmount(ether_eth).isPresent());
        assertEquals(new Double(12.12345678), a1.doubleAmount(ether_eth).get());

        Amount a2 = Amount.create( "123.12345678", false, ether_eth).get();
        assertTrue(a2.doubleAmount(ether_eth).isPresent());
        assertEquals(new Double(123.12345678), a2.doubleAmount(ether_eth).get());

        Amount a3 = Amount.create( "12.12345678", false, gwei_eth).get();
        assertTrue(a3.doubleAmount(ether_eth).isPresent());
        assertEquals(new Double(12.12345678), a3.doubleAmount(gwei_eth).get());

        Amount a4 = Amount.create( "123.12345678", false, gwei_eth).get();
        assertTrue(a4.doubleAmount(ether_eth).isPresent());
        assertEquals(new Double(123.12345678), a4.doubleAmount(gwei_eth).get());

        Amount a5 = Amount.create( "1.234567891234567891", false, ether_eth).get();
        assertEquals(new Double(1234567891234567891L), a5.doubleAmount(wei_eth).get());
        // TODO: The swift behaviour diverges here; how do we want to handle?
        // assertEquals("wei1,234,567,891,234,570,000", a5.toStringAsUnit(wei_eth, null).get());
        assertEquals("1234567891234567891", a5.toStringWithBase(10, ""));

        Amount a6 = Amount.create("1", false, ether_eth).get();
        assertEquals("1000000000000000000", a6.toStringWithBase(10, ""));
        assertEquals("0xDE0B6B3A7640000".toLowerCase(), a6.toStringWithBase(16, "0x"));
    }
}
