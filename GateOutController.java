package com.pertamina.tas.sc;

import java.util.Date;
import java.util.HashMap;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import com.pertamina.tas.JembatanTimbang;
import com.pertamina.tas.OrderPengiriman;
import com.pertamina.tas.OrderPengirimanIndustri;
import com.pertamina.tas.OrderPengirimanSPPBE;
import com.pertamina.tas.PenjualanMobil;
import com.pertamina.tas.StatusPenjualanMobil;
import com.pertamina.tas.dao.PenjualanMobilDAO;
import com.pertamina.tas.dao.WeightBridgeDAO;
import com.pertamina.tas.sap.DokumenSPA;
import com.pertamina.tas.sap.OperasiCompleteSPA;
import com.pertamina.tas.sap.OperasiCreateDOFromPO;
import com.pertamina.tas.sap.OperasiGISppbe;
import com.pertamina.tas.util.HibernateUtil;
import com.pertamina.tas.util.SAPUtil;

public class GateOutController extends SelectorComposer<Window> {
	private static final long serialVersionUID = 6296420115605088983L;
	private PenjualanMobilDAO mDao = new PenjualanMobilDAO(Sessions.getCurrent());
	// private FillingPointDAO fDao = new FillingPointDAO(Sessions.getCurrent());
	private WeightBridgeDAO wDao = new WeightBridgeDAO(Sessions.getCurrent());
	@Wire private Textbox txtPin;
	@Wire private Intbox txtWeight;
	@Wire private Label lblNopol;
	@Wire private Label lblNomorPO;
	@Wire private Label lblNomorPOx;
	@Wire private Label lblPemilik;
	@Wire private Label lblNomorSPA;
	@Wire private Label lblNomorSPAx;
	@Wire private Label lblSopir;
	@Wire private Label lblPelanggan;
	@Wire private Label lblKapasitas;
	@Wire private Label lblProduk;
	@Wire private Label lblBeratKosong;
	@Wire private Label lblUomBeratKosong;
	@Wire
	private Label lblUomKapasitas;
	@Wire
	private Label lblSisa;
	@Wire
	private Label lblSisax;
	@Wire
	private Label lblFillingPoint;
	@Wire
	private Textbox txtSegel;
	@Wire
	private Button btnWeightOut;
	@Wire
	private Button btnBPPK;
	private PenjualanMobil penjualanMobil = null;
	private boolean lanjut = false;

	public GateOutController() {
		super();
	}

	@Listen("onTimer = #timer")
	public void onTimer(Event evt) {
		// TODO sesuaikan dengan jembatan timbang yang mana
		JembatanTimbang jembatanTimbang = wDao.getById(1);
		wDao.refresh(jembatanTimbang);
		txtWeight.setValue(jembatanTimbang.getNilai());
	}

	@Listen("onOK = #txtPin")
	public void pinInserted(Event evt) {
		PenjualanMobil pm = mDao.getByPIN(txtPin.getValue(), StatusPenjualanMobil.WEIGHT_OUT);
		tampilkanDetil(pm);
		if (pm != null) {} else {
			Clients.showNotification("pin tidak terdaftar", "error", txtPin, "end_after", 3000, true);
		}
		penjualanMobil = pm;
		txtPin.setValue("");
	}

	private void tampilkanDetil(PenjualanMobil pm) {
		lblNomorPOx.setValue(Labels.getLabel("modul.operation.gate.code"));
		lblNomorSPAx.setValue(Labels.getLabel("modul.operation.gate.spa"));
		lblSisax.setVisible(true);
		lblSisa.setVisible(true);
		if (pm != null && pm.getOrderPengiriman() != null) {
			OrderPengiriman order = pm.getOrderPengiriman();
			if (order instanceof OrderPengirimanSPPBE) {
				OrderPengirimanSPPBE orderSPPBE = (OrderPengirimanSPPBE) order;
				lblNomorPO.setValue(pm == null ? null : orderSPPBE.getOrderPembelian().getPoNumber());
				lblSisa.setValue(orderSPPBE == null ? null : String.valueOf(orderSPPBE.getOrderPembelian().getSisa() + " " + orderSPPBE.getUom()));
			} else if (order instanceof OrderPengirimanIndustri) {
				OrderPengirimanIndustri opIndustri = (OrderPengirimanIndustri) pm.getOrderPengiriman();
				lblNomorPOx.setValue(Labels.getLabel("modul.operation.gate.doNumber"));
				lblNomorPO.setValue(opIndustri.getKode());
				lblNomorSPAx.setValue(Labels.getLabel("modul.operation.gate.soNumber"));
				lblSisax.setVisible(false);
				lblSisa.setVisible(false);
			}
			lblNomorSPA.setValue(order == null ? null : order.getPrintableOrderNumber());
		} else {
			lblNomorPO.setValue(null);
			lblNomorSPA.setValue(null);
			txtSegel.setValue(null);
			txtPin.setValue(null);
		}
		lblNopol.setValue(pm == null ? null : pm.getMobil().getPlatNomer());
		lblPemilik.setValue(pm == null ? null : pm.getMobil().getPemilik().getNama());
		lblSopir.setValue(pm == null ? null : pm.getMobil().getSopir().getNama());
		lblPelanggan.setValue(pm == null ? null : pm.getPelanggan().getNama());
		lblKapasitas.setValue(pm == null ? null : String.valueOf(pm.getMobil().getKapasitas()));
		lblProduk.setValue(pm == null ? null : pm.getProduk().getNama());
		lblBeratKosong.setValue(pm == null ? null : String.valueOf(pm.getMobil().getBeratKosong()));
		lblUomKapasitas.setValue(pm == null ? null : pm.getMobil().getUom());
		lblUomBeratKosong.setValue(pm == null ? null : pm.getMobil().getUom());
		lblFillingPoint.setValue(pm == null ? null : pm.getFillingPoint().getNama());
		btnWeightOut.setDisabled(pm == null);
		//btnBPPK.setDisabled(pm == null);
		txtSegel.setDisabled(pm == null);
	}

	@Override
	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);
		onTimer(null);
		// cboFillingPoint.setItemRenderer(new ComboitemRenderer<FillingPoint>() {
		// @Override
		// public void render(Comboitem item, FillingPoint data, int index) throws Exception {
		// item.setLabel(data.getNama());
		// }});
		tampilkanDetil(null);
	}

	@Listen("onClick = #btnWeightOut")
	public void btnWeightOutClicked() {
		mDao.refresh(penjualanMobil);
		penjualanMobil.setBeratWeightOut(txtWeight.getValue());
		penjualanMobil.setWaktuWeightOut(new Date());
		penjualanMobil.setStatus(StatusPenjualanMobil.LOCAL_COMPLETED);
		penjualanMobil.setNomorSegel(txtSegel.getValue());
		mDao.save(penjualanMobil);

		OrderPengiriman op = penjualanMobil.getOrderPengiriman();
		boolean terus = false;
		
		if (op instanceof OrderPengirimanSPPBE) {
			OrderPengirimanSPPBE order = (OrderPengirimanSPPBE) op;
			DokumenSPA spa = order.getSuratPerintahAngkut();

			try {
				SAPUtil sapUtil = SAPUtil.getInstance();
				Session sess = HibernateUtil.getSession(Sessions.getCurrent());
				// create DO from PO
				if (order.getKodeSAP() == null){
					String nomorDO = null;
					try {
						nomorDO = sapUtil.createDO(sess, order, String.valueOf(penjualanMobil.getJumlah() / 1000));
					} catch (Exception e) {
						e.printStackTrace();
                                                Clients.showNotification("Pembuatan nomor DO gagal", true);
                                                btnBPPK.setDisabled(false);
					}
					if (nomorDO == null || nomorDO.length() < 1){
						OperasiCreateDOFromPO sapOp = new OperasiCreateDOFromPO(penjualanMobil);
						Transaction tr = sess.beginTransaction();
						sess.saveOrUpdate(sapOp);
						tr.commit();
					} else {
						// GI SPPBE
						try {
							terus = sapUtil.processGISPPBE(sess, penjualanMobil);
                                                } catch (Exception e) {
							e.printStackTrace();
                                                        Clients.showNotification("GI gagal", true);
                                                        btnBPPK.setDisabled(false);
						}
						if (!terus){
							OperasiGISppbe sapOp = new OperasiGISppbe(penjualanMobil);
							Transaction tr = sess.beginTransaction();
							sess.saveOrUpdate(sapOp);
							tr.commit();
						} else if ((spa != null) && !spa.getTypeSPA().equals("TAS")) { 
							// complete spa, if using SPA
							try {
								terus = sapUtil.completeSPA(sess, penjualanMobil);
							} catch (Exception e) {
								e.printStackTrace(); 
                                                                Clients.showNotification("Complete SPA gagal", true);
                                                                btnBPPK.setDisabled(false);
							}
							if (!terus) {
								OperasiCompleteSPA sapOp = new OperasiCompleteSPA(penjualanMobil);
								Transaction tr = sess.beginTransaction();
								sess.saveOrUpdate(sapOp);
								tr.commit();
							}
						} else {
							System.out.println("complete SPA not executed : no SPA found or SPA type equal to TAS");
						}					
					}
				}
				// update data PO
				sapUtil.updateDetailPO(sess, order.getOrderPembelian());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (op instanceof OrderPengirimanIndustri) {
			OrderPengirimanIndustri order = (OrderPengirimanIndustri) op;
			try {
				SAPUtil sapUtil = SAPUtil.getInstance();
				Session sess = HibernateUtil.getSession(Sessions.getCurrent());
				terus = sapUtil.processGIIndustri(sess, penjualanMobil);
				sapUtil.updateDOIndustri(sess, order.getKodeSAP(), "");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		//if (terus || !terus) {
                if (terus || true) {
			Clients.showBusy("Printing loading info ...");
			HashMap<String, Object> map = new HashMap<>();
			map.put("idPenjualan", penjualanMobil.getId());
			Window window = ((Window) Executions.getCurrent().createComponents("WEB-INF/laporan-buktipengiriman.zul", getSelf(), map));
			//Window window = ((Window) Executions.getCurrent().createComponents("WEB-INF/laporan-bppk.zul", getSelf(), map));
			window.doModal();
			// currentPenjualanMobil = null;
			//tampilkanDetil(null);
			Clients.clearBusy();
			btnBPPK.setDisabled(false);
			lanjut = true;
			//if (window.is)
		}
		/*if (lanjut) {
			Clients.showBusy("Printing BPPK ...");
			HashMap<String, Object> map = new HashMap<>();
			map.put("idPenjualan", penjualanMobil.getId());
			Window window1 = ((Window) Executions.getCurrent().createComponents("WEB-INF/laporan-bppk.zul", getSelf(), map));
			window1.doModal();
			// currentPenjualanMobil = null;
			tampilkanDetil(null);
			Clients.clearBusy();
		}*/
	}
	
	@Listen("onClick = #btnBPPK")
	public void btnBPPKClicked() {
		//if (lanjut) {
			Clients.showBusy("Printing BPPK ...");
			HashMap<String, Object> map1 = new HashMap<>();
			map1.put("idPenjualan", penjualanMobil.getId());
			//Window window1 = ((Window) Executions.getCurrent().createComponents("WEB-INF/laporan-buktipengiriman.zul", getSelf(), map1));
			Window window1 = ((Window) Executions.getCurrent().createComponents("WEB-INF/laporan-bppk.zul", getSelf(),map1));
			window1.doModal();
			// currentPenjualanMobil = null;
			tampilkanDetil(null);
			Clients.clearBusy();
		//}
	}
}
