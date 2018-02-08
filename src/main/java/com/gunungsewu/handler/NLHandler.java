package com.gunungsewu.handler;

import java.io.InputStream;
import java.io.InvalidObjectException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util	.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.interfaces.BotApiObject;
import org.telegram.telegrambots.api.methods.GetFile;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Document;
import org.telegram.telegrambots.api.objects.File;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.PhotoSize;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gunungsewu.BotConfig;
import com.gunungsewu.Commands;
import com.gunungsewu.database.entity.CheckOrderChat;
import com.gunungsewu.database.entity.Issue;
import com.gunungsewu.database.entity.KeluhanChat;
import com.gunungsewu.database.entity.NLUser;
import com.gunungsewu.database.entity.ReorderChat;
import com.gunungsewu.database.entity.SessionHolder;
import com.gunungsewu.database.service.Constant;
import com.gunungsewu.database.service.ConverstationService;
import com.gunungsewu.database.service.NLBotService;
import com.gunungsewu.rabbit.MQUtil;
import com.gunungsewu.rabbit.ws.Order;
import com.gunungsewu.rabbit.ws.OrderItem;
import com.gunungsewu.rabbit.ws.OrderItem.Size;
import com.gunungsewu.rabbit.ws.POType;
import com.gunungsewu.services.Emoji;
import com.gunungsewu.services.LocalisationService;

/**
 * @author Lambok Sianturi
 * @version 1.0
 * @brief Handler for updates to Files Bot This bot is an example for the use of
 *        sendMessage asynchronously
 * @date 24 of June of 2015
 */
public class NLHandler extends TelegramLongPollingBot {
	Logger log = LoggerFactory.getLogger(NLHandler.class);

	private static final int REQUEST_PHONE_STATUS = 0;
	private static final int CEK_ORDER_1_ASK_PO_NO  = 1;
	private static final int CEK_ORDER_2_ASK_METHOD = 11;
	private static final int CEK_ORDER_3_CHOOSE_PO  = 111;
	private static final int REORDER_1_ASK_PO_NO      = 2;
	private static final int REORDER_2_ASK_ORDER_ID   = 21;
	private static final int REORDER_3_ASK_SIZE       = 211;
	private static final int REORDER_4_CONFIRM_SIZE   = 2111;
	private static final int REORDER_4_ASK_NEW_PO_NO  = 21111;
	private static final int REORDER_4_CONFIRM_NEW_PO = 211111;
	private static final int REORDER_5_SAVE_REORDER   = 2111111;

	private static final int ISSUE_1_ASK_PO = 3;
	private static final int ISSUE_2_ASK_ORDER_ID = 31;
	private static final int ISSUE_3_ASK_TOPIC = 311;
	private static final int ISSUE_4_ASK_DETAIL = 3111;
	private static final int ISSUE_5_FIRST_IMAGE = 31111;
	private static final int ISSUE_6_SECOND_IMAGE = 311111;
	private static final int ISSUE_7_THIRD_IMAGE = 3111111;
	private static final int SOLAR_STATUS = 5;
	private static final int SOLAR_STATUS_ANSWER = 51;

	private static final String STATUS_KIRIM_YES="Y";
//	private static final String STATUS_KIRIM_NO="N";
	
	private static final String STRING_YES = "Ya";
	private static final String STRING_NO = "Tidak";
	private static final String STRING_FINISH = "FINISH";
	
	private static final String SANDAR = "Kapal Sandar";
	private static final String BONGKAR = "Mulai Bongkar";
	private static final String MUAT = "Mulai muat";
			
	private static final String TELEGRAM_URL = "https://api.telegram.org/file/bot";
	
	@Override
	public String getBotUsername() {
		return BotConfig.NL_CS_USER;
	}

	// private static final DecimalFormat DF = new DecimalFormat("#");
	private final String SIZE_DELIMITER = ", ";

	private final NLBotService service = NLBotService.getInstance();
	private final ConverstationService convSvc = ConverstationService.getInstance();

	final HashMap<Integer, SessionHolder> session = new HashMap<>();

	public NLHandler() {}

	@Override
	public String getBotToken() {
		return BotConfig.NL_CS_TOKEN;
	}
	
	private boolean isValidUser(final Integer userId) {
		SessionHolder holder = session.get(userId);
		if (holder != null && holder.getUser() != null) {
			try {
				return checkUserByChatId(userId, "en");
			} catch (Exception e) {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public void onUpdateReceived(Update update) {
		boolean validUser = false;
		Integer userId = 0;
		try {
			log.debug("MSG: " + update.getMessage().getText());
		} catch (Exception e) {
		}
		try {
			if (update.hasMessage()) {
				userId = update.getMessage().getFrom().getId();
				System.out.println("MSG: " + update.getMessage().getText());
			} else if (update.hasCallbackQuery()) {
				System.out.println("MSG: " + update.getCallbackQuery().getData());
				userId = update.getCallbackQuery().getFrom().getId();
			}
			System.out.println("USER ID: " + userId);
			if (update.hasMessage()) {
				try {
					handleIssueUpdate(update.getMessage());
				} catch (TelegramApiRequestException e) {
					if (e.getApiResponse().contains("Bot was blocked by the storeUser")) {
						if (update.getMessage().getFrom() != null) {
							resetUserStatus(update.getMessage().getFrom().getId());
						}
					}
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			} else if (update.hasCallbackQuery()) {
				handleIssueCallback(update);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	private boolean checkUserByMsisdn(Message message, String language)
			throws InvalidObjectException, TelegramApiException {
		boolean result = false;
		String msisdn = null; NLUser user = null;

		if (message.getContact() != null) {
			msisdn = message.getContact().getPhoneNumber().replace("+", "");
			user = service.getUserByMsisdn(msisdn);
			if (user != null) {
				session.put(message.getFrom().getId(), new SessionHolder(user));
				result = true;
				sendHelpMessage(message, language);
				updateUserStatus(message.getFrom().getId(), REQUEST_PHONE_STATUS);
				service.updateUserChatId(msisdn, message.getFrom().getId());
			} else {
				sendNotRegisteredMessage(message, language);
			}
		} else {
			requestPhoneNumber(message, language);
		}
		
		return result;
	}

	private void requestPhoneNumber(Message message, String language)
			throws InvalidObjectException, TelegramApiException {
//		updateUserStatus(message.getFrom().getId(), REQUEST_PHONE_STATUS);
		SendMessage sendMsg = new SendMessage();

		String formatedString = String.format(LocalisationService.getInstance().getString("welcome", language),
				message.getFrom().getFirstName());
		sendMsg.setText(formatedString);
		sendMsg.setChatId(message.getChatId());
		execute(sendMsg);
		
		sendMsg.setText(LocalisationService.getInstance().getString("welcome2", language));
		sendMsg.setReplyMarkup(getPhoneNoKeyboard());
		execute(sendMsg);
//		sendMessageRequest.setText(LocalisationService.getInstance().getString("welcome3", language));
//		execute(sendMessageRequest);
	}

	private void handleIssueCallback(Update update) throws InvalidObjectException, TelegramApiException {
		CallbackQuery callback = update.getCallbackQuery();
		String language = "en";
		log.debug("MSG: " + update.getCallbackQuery().getData());
		if (callback != null) {
			if (getUserStatus(update.getCallbackQuery().getFrom().getId()) == CEK_ORDER_1_ASK_PO_NO ) {
				onCheckOrderStep2(update.getCallbackQuery(), language);
			}else if (getUserStatus(update.getCallbackQuery().getFrom().getId()) == CEK_ORDER_2_ASK_METHOD) {
				askPOType(callback.getData(), callback.getFrom().getId(), CEK_ORDER_3_CHOOSE_PO, language);
			}else if (getUserStatus(update.getCallbackQuery().getFrom().getId()) == CEK_ORDER_3_CHOOSE_PO) {
				replyWithOrderStatus(callback.getData(), update.getCallbackQuery().getFrom().getId(), language);
			} else if (getUserStatus(update.getCallbackQuery().getFrom().getId()) == REORDER_2_ASK_ORDER_ID) {
				showOrderDetail(callback.getData(), update.getCallbackQuery().getFrom().getId(), REORDER_3_ASK_SIZE,
						true, language);
			} else if (getUserStatus(update.getCallbackQuery().getFrom().getId()) == REORDER_3_ASK_SIZE) {
				askSizeDetail(callback.getData(), update.getCallbackQuery().getFrom().getId(), language);
			} else if (getUserStatus(update.getCallbackQuery().getFrom().getId()) == ISSUE_2_ASK_ORDER_ID) {
				showOrderDetail(callback.getData(), update.getCallbackQuery().getFrom().getId(), ISSUE_3_ASK_TOPIC,
						true, language);
			} else if (getUserStatus(update.getCallbackQuery().getFrom().getId()) == ISSUE_3_ASK_TOPIC) {
				chooseTopic(callback.getData(), update.getCallbackQuery().getFrom().getId(), language);
			} else if (getUserStatus(update.getCallbackQuery().getFrom().getId()) == REORDER_4_CONFIRM_SIZE) {
				processSizeConfirmation(update.getCallbackQuery().getFrom().getId(), update.getCallbackQuery().getData());
			} else if (getUserStatus(update.getCallbackQuery().getFrom().getId()) == REORDER_4_CONFIRM_NEW_PO) {
				saveReorder(callback.getData(), update.getCallbackQuery().getFrom().getId(), language);
			} else if (getUserStatus(update.getCallbackQuery().getFrom().getId()) == SOLAR_STATUS_ANSWER) {
				onSolar3(callback, language);
			} else if (callback.getData().equals(STRING_FINISH)) {
				finish(update.getCallbackQuery().getFrom(), update.getCallbackQuery().getMessage(), language);
			} else {
				cannotUnderstand(callback.getMessage(), language);
			}
		}
	}

	private void handleIssueUpdate(Message message) throws InvalidObjectException, TelegramApiException {
		if (message.getText().startsWith("q ") || message.getText().startsWith("Q ")) {
			convSvc.saveChat(message.getChatId(), message.getFrom().getId(), getFullName(message), "Q", message.getText().substring(2, message.getText().length()));
		} else if (message.getText().startsWith("a ") || message.getText().startsWith("A ")) {
			convSvc.saveChat(message.getChatId(), message.getFrom().getId(), getFullName(message), "A", message.getText().substring(2, message.getText().length()));
		}
	}
	
	private String getFullName(Message msg) {
		return msg.getFrom().getFirstName() + msg.getFrom().getLastName();
	}
	
	private void showOrderDetail(String poType, Integer chatId, Integer phase, boolean isFirst, String language)
			throws InvalidObjectException, TelegramApiException {
		SendMessage sendMsg = new SendMessage();
		SendPhoto sendPhoto = new SendPhoto();
		String poNumber = session.get(chatId).getOrder().getPoNumber();

		// http client untuk ambil gambar
		HttpClient client = HttpClients.createDefault();
		HttpGet get = new HttpGet();

		StringBuilder msg = null;
		StringBuilder sizeQty = null;
		int qty = 0;
		int count = 0;
		List<OrderItem> items = null;
		try {
			if (isFirst) {
//				items = service.getReOrder(poType, poNumber, ""+chatId);
				if (phase == REORDER_3_ASK_SIZE) {
					session.get(chatId).getOrder().setItems(items);
					session.get(chatId).getNewOrder().setItems(new ArrayList<>());
				}
			} else {
				items = session.get(chatId).getOrder().getItems();
			}
			if (items != null && items.size() > 0) {

				for (OrderItem item : items) {
					msg = new StringBuilder();
					sizeQty = new StringBuilder();
					qty = 0;
					count = 0;
					msg.append("No Order: " + item.getOrderId() + "\n");
					msg.append("Desc: " + item.getDescription() + "\n");
					for (OrderItem.Size s : item.getSizes()) {
						qty += s.getQuantity();
						sizeQty.append(s.getSize()).append("=").append(s.getQuantity().intValue());
						if (++count < item.getSizes().size()) {
							sizeQty.append(SIZE_DELIMITER);
						}
					}
					msg.append("Size Qty: " + sizeQty.toString() + " [" + qty + " " + item.getUom() + "]");

					sendMsg.setText(msg.toString());
					sendMsg.setChatId(chatId.longValue());
					if (phase == REORDER_3_ASK_SIZE)
						sendMsg.setReplyMarkup(getROButton(item.getOrderId(), true, isFirst));
					else
						sendMsg.setReplyMarkup(getROButton(item.getOrderId(), false, isFirst));

					if (item.getImage().trim().equals("")) {
						execute(sendMsg);
					} else {
						sendPhoto.setChatId(chatId.longValue());
						sendPhoto.setChatId("" + chatId);
						get.setURI(new URI(item.getImage()));
						HttpResponse response = client.execute(get);
						InputStream is = response.getEntity().getContent();
						sendPhoto.setNewPhoto(item.getOrderId(), is);
						sendPhoto(sendPhoto);
						is.close();
						execute(sendMsg);
					}
				}
				updateUserStatus(chatId, phase);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}

	}

	/**
	 * @param qty
	 * @param chatId
	 * @param language
	 * @throws InvalidObjectException
	 * @throws TelegramApiException
	 */
	
	private void captureSizeAndConfirm(String qty, Integer chatId, String language)
			throws InvalidObjectException, TelegramApiException {
		
		List<OrderItem.Size> items = new ArrayList<>();
		OrderItem.Size sz = null;
		SendMessage sendMsg = new SendMessage();
		sendMsg.setChatId("" + chatId);
		String[] sizes = session.get(chatId).getCurrentSize().split(SIZE_DELIMITER.trim());
		String[] qtys = qty.split(SIZE_DELIMITER.trim());
		Integer q = 0;
		if (qtys.length > 0 && sizes.length > 0) {
			if (sizes.length >= qtys.length) {
				for (int i = 0; i < qtys.length; i++) {
					try {
						q = Integer.parseInt(qtys[i].trim());
						if (q > 0) {
							sz = new OrderItem.Size();
							sz.setSize(sizes[i].trim());
							sz.setQuantity(q);
							items.add(sz);
						}
					} catch (Exception e) {}
				}
				session.get(chatId).getCurrentOrderItem().setSizes(items);
				
			}
		} else {
			sendMsg.setText("Qty yang Anda masukkan salah, mohon dimasukkan kembali.");
			execute(sendMsg);
			return;
		}
		
		StringBuilder sizeQty = new StringBuilder();
		int count=0; q=0;
		for (OrderItem.Size s : items) {
			q += s.getQuantity();
			sizeQty.append(s.getSize()).append("=").append(s.getQuantity().intValue());
			if (++count < items.size()) {
				sizeQty.append(SIZE_DELIMITER);
			}
		}
		if (q > 0) {
			sendMsg.setText("Size qty yang Anda masukkan: " + sizeQty.toString() + ".\nApakah size qty baru sudah benar?");
//			sendMsg.setReplyMarkup(getYesNoKeyboard());
			sendMsg.setReplyMarkup(getOptionButtons(true, STRING_YES, STRING_NO));
			updateUserStatus(chatId, REORDER_4_CONFIRM_SIZE);
		} else {
			sendMsg.setText("Qty yang Anda masukkan salah, mohon dimasukkan kembali.");
			updateUserStatus(chatId, REORDER_3_ASK_SIZE);
		}
		execute(sendMsg);
	}
	
	private void processSizeConfirmation(Integer chatId, String confirmation)
			throws InvalidObjectException, TelegramApiException {
		int idx =0;
		String orderId = session.get(chatId).getCurrentOrderItem().getOrderId();
		
		if (confirmation.equals(STRING_YES)) {
			session.get(chatId).getNewOrder().getItems().add(session.get(chatId).getCurrentOrderItem());
			
//			List<OrderItem> oItems = session.get(chatId).getOrder().getItems();
			List<OrderItem> items = session.get(chatId).getOrder().getItems();
			if (items.size() > 0) {
				
				for (idx = 0; idx < items.size(); idx++) {
					if (items.get(idx).getOrderId().equals(orderId)) {
						break;
					}
				}
				items.remove(idx);
				
				if (items.size() > 0) {
					SendMessage sendMsg = new SendMessage();
					sendMsg.setText("Apakah mau order item yang lain?");
					sendMsg.setChatId("" + chatId);
					execute(sendMsg);
					showOrderDetail("", chatId, REORDER_3_ASK_SIZE, false, "en");
				} else {
					askNewPONumber(chatId, "en");
				}
			} else {
				askNewPONumber(chatId, "en");
			}
		} else {
			askSizeDetail(orderId, chatId, "en");
		}
	}
	
	private void askNewPONumber(Integer chatId, String language) throws InvalidObjectException, TelegramApiException {
		updateUserStatus(chatId, REORDER_4_ASK_NEW_PO_NO);
		SendMessage sendMsg = new SendMessage();

		sendMsg.setText("Silakan masukkan nomor PO baru Anda untuk pesanan ini.");
		sendMsg.setChatId("" + chatId);
		execute(sendMsg);
	}

	
	
	private void saveReorder(String confirm, Integer chatId, String language) throws InvalidObjectException, TelegramApiException {
		
		if (confirm.equals(STRING_YES)) {
			List<OrderItem> items = session.get(chatId).getNewOrder().getItems();
			List<OrderItem.Size> sizes = null;
			Integer sum = 0;
			for (OrderItem item : items) {
				sum = 0;
				sizes = item.getSizes();
				for (Size s : sizes) {
					sum += s.getQuantity();
				}
				item.setQtyPesan(sum);
			}
			
			
			SendMessage sendMsg = new SendMessage();
	
			sendMsg.setText("Order Anda sudah kami terima, secepatnya Anda akan terima konfirmasi dari kami.");
			sendMsg.setChatId("" + chatId);
	
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(Include.NON_NULL);
	
			try {
				String str = mapper.writeValueAsString(session.get(chatId).getNewOrder());
				log.debug("New Order: " + str);
				MQUtil.getInstance().postOrderToMq(str);
				session.get(chatId).setNewOrder(null);
				session.get(chatId).setCurrentOrderItem(null);
				execute(sendMsg);
				// session.get(chatId).setOrder(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				ReorderChat chat =  (ReorderChat) session.get(chatId).getChat().get(Constant.REORDER_CHAT);
				chat.setStatus(1);
				convSvc.updReorderStatus(chat);
			} catch(Exception e) {
				log.error(e.getMessage());
			}
		} else {
			askNewPONumber(chatId, language);
		}
		
	}

	private void askSizeDetail(String orderId, Integer chatId, String language)
			throws InvalidObjectException, TelegramApiException {
		SendMessage sendMsg = new SendMessage();

		StringBuilder msg = null;
		StringBuilder size = null;
		StringBuilder sizeQty = null;
		int count = 0;
		int idx = 0;
		OrderItem item = null;

		if (orderId.equals(STRING_FINISH)) {
			askNewPONumber(chatId, language);
			return;
		}

		try {
			session.get(chatId).setCurrentOrderItem(new OrderItem());
			session.get(chatId).getCurrentOrderItem().setOrderId(orderId);

			List<OrderItem> items = session.get(chatId).getOrder().getItems();
			if (items != null && items.size() > 0) {
				for (idx = 0; idx < items.size(); idx++) {
					if (items.get(idx).getOrderId().equals(orderId)) {
						item = items.get(idx);
						break;
					}
				}

				msg = new StringBuilder();
				size = new StringBuilder();
				sizeQty = new StringBuilder();
				for (OrderItem.Size s : item.getSizes()) {
					size.append(s.getSize());
					sizeQty.append(s.getSize()).append("=").append(s.getQuantity().intValue());
					if (++count < item.getSizes().size()) {
						size.append(SIZE_DELIMITER);
						sizeQty.append(SIZE_DELIMITER);
					}
				}
				// System.out.println("Size: " + size);
				session.get(chatId).setCurrentSize(size.toString());

				msg.append("Order Lama: " + item.getDescription() + "\n");
				msg.append("Size Qty: " + sizeQty + " " + item.getUom() + "\n");
				sendMsg.setText(msg.toString());
				sendMsg.setChatId(chatId.longValue());
				execute(sendMsg);
				
				if (item.getSizes().size() > 1) {
					sendMsg.setText("Masukkan jumlah (qty) untuk tiap ukuran (size) dengan format sesuai urutan sizenya. Contoh: untuk reorder S=20, M=10, L=30 cukup masukkan: 20,10,30");
				} else {
					sendMsg.setText("Masukkan jumlah (qty) untuk ukuran (size): " + size);
				}

				execute(sendMsg);

//				items.remove(idx);

				updateUserStatus(chatId, REORDER_3_ASK_SIZE);
			} else {
				updateUserStatus(chatId, REORDER_5_SAVE_REORDER);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}

	}

	private void replyWithOrderStatus(String poType, Integer chatId, String language)
			throws InvalidObjectException, TelegramApiException {
		SendMessage sendMsg = new SendMessage();
		SendPhoto sendPhoto = new SendPhoto();
		String poNumber = session.get(chatId).getOrder().getPoNumber();

		HttpClient client = HttpClients.createDefault();
		HttpGet get = new HttpGet();

		StringBuilder msg = null;
		StringBuilder sizeQty = null;
		int count = 0; int qty=0;
		try {
			List<OrderItem> items = null;
//			items = service.getOrder(poType, poNumber, ""+chatId);
			for (OrderItem item : items) {
				msg = new StringBuilder();
				msg.append("No Order: " + item.getOrderId() + "\n");
				msg.append("Desc: " + item.getDescription() + "\n");
				
				sizeQty = new StringBuilder();
				count = 0;
				qty = 0;
				for (OrderItem.Size s : item.getSizes()) {
					qty += s.getQuantity();
					sizeQty.append(s.getSize()).append("=").append(s.getQuantity().intValue());
					if (++count < item.getSizes().size()) {
						sizeQty.append(SIZE_DELIMITER);
					}
				}
				msg.append("Size Qty: " + sizeQty.toString() + " [" + qty + " " + item.getUom() + "].\n");
				if (item.getStatusKirim() != null && item.getStatusKirim().equals(STATUS_KIRIM_YES)) {
					msg.append("Terkirim : " + item.getTglKirim());
				} else {
					msg.append("Estimasi Kirim: "+ item.getTglKirim());
				}
				sendMsg.setText(msg.toString());
				sendMsg.setChatId("" + chatId);

				if (item.getImage().trim().equals("")) {
					execute(sendMsg);
				} else {
					get.setURI(new URI(item.getImage()));
					HttpResponse response = client.execute(get);
					InputStream is = response.getEntity().getContent();
					sendPhoto.setChatId("" + chatId);
					sendPhoto.setNewPhoto(item.getOrderId(), is);
					// sendPhoto.setPhoto(item.getImage());
					// sendPhoto.setPhoto("http://www.national-label.com/img/ProductHome/Label%20Sepatu.jpg");
					// sendPhoto.setCaption(msg.toString());
					sendPhoto(sendPhoto);
					is.close();
					execute(sendMsg);
				}
			}
			resetUserStatus(chatId);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}

	}

	private void onCheckOrderStep1(Message message, String language)
			throws InvalidObjectException, TelegramApiException {
		updateUserStatus(message.getFrom().getId(), CEK_ORDER_1_ASK_PO_NO);
		
		SendMessage sendMsg = new SendMessage();
		sendMsg.setText(LocalisationService.getInstance().getString("sandar1", language));
		sendMsg.setChatId(message.getChatId());
		sendMsg.setReplyMarkup(getOptionButtons(false, SANDAR, BONGKAR, MUAT ));
		execute(sendMsg);
	}
	
	private void onCheckOrderStep2(CallbackQuery query, String language)
			throws InvalidObjectException, TelegramApiException {
		
		SendMessage sendMsg = new SendMessage();
		
		sendMsg.setText("Aktivitas " + query.getFrom().getFirstName() + " "+query.getFrom().getLastName() + ": " + query.getData() + " sudah disimpan.");
		sendMsg.setChatId(query.getMessage().getChatId());
		execute(sendMsg);
		
//		answerInlineQuery(converteResultsToResponse(inlineQuery, results))
		
	}

	/*private static AnswerInlineQuery converteResultsToResponse(InlineQuery inlineQuery, List<RaeService.RaeResult> results) {
        AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery();
        answerInlineQuery.setInlineQueryId(inlineQuery.getId());
        answerInlineQuery.setCacheTime(CACHETIME);
        answerInlineQuery.setResults(convertRaeResults(results));
        return answerInlineQuery;
    }*/
	private void askPOType(String po, Integer chatId, Integer phase, String language)
			throws InvalidObjectException, TelegramApiException {

		session.get(chatId).setOrder(new Order(po));
		// session.get(chatId).setIssue(new Issue());
		if (session.get(chatId).getIssue() != null) {
			session.get(chatId).getIssue().setPoNo(po);
		}
		updateUserStatus(chatId, phase);

		SendMessage sendMsg = new SendMessage();
		String formatedString = LocalisationService.getInstance().getString("pilihPOType", language);

		sendMsg.setChatId(""+chatId);

		List<POType> types = null;
//		types = service.getPOTypes(Constant.GET_PO, po, ""+chatId);
		if (types != null && types.size() > 0) {
			sendMsg.setText(formatedString);
			sendMsg.setReplyMarkup(getPOTypeKeyboard(types));
		} else {
			sendMsg.setText("PO yang Anda masukkan: " + po +" tidak ditemukan. Coba kirimkan kembali nomor PO Anda.");
			updateUserStatus(chatId, CEK_ORDER_2_ASK_METHOD);
		}

		execute(sendMsg);
		
		if (phase == CEK_ORDER_3_CHOOSE_PO) {
			try {
				CheckOrderChat chat =  (CheckOrderChat) session.get(chatId).getChat().get(Constant.CHECK_ORDER_CHAT);
//				chat.setPoNo(po);
				convSvc.updCheckPo(chat);
			} catch(Exception e) {
				log.error(e.getMessage());
			}
		} else {
			try {
				KeluhanChat chat =  (KeluhanChat) session.get(chatId).getChat().get(Constant.KELUHAN_CHAT);
//				chat.setPoNo(po);
				convSvc.updKelPo(chat);
			} catch(Exception e) {
				log.error(e.getMessage());
			}
		}

	}

	private void onReorderStep1(Message message, String language) throws InvalidObjectException, TelegramApiException {
		updateUserStatus(message.getFrom().getId(), REORDER_1_ASK_PO_NO);

		SendMessage sendMessageRequest = new SendMessage();
		sendMessageRequest.setText(LocalisationService.getInstance().getString("nomorPOReorder", language));
		sendMessageRequest.setChatId(message.getChatId());
		// sendMessageRequest.setReplyMarkup(getStoreLocationMenuKeyboard("en"));
		execute(sendMessageRequest);
		
		try {
			ReorderChat chat = null;
//			new ReorderChat(message.getFrom().getId(), message.getText());
			convSvc.newReorder(chat);
			session.get(message.getFrom().getId()).getChat().put(Constant.REORDER_CHAT, chat);
		} catch(Exception e) {
			log.error(e.getMessage());
		}
	}

	private void onReorderStep2(Message message, String language) throws InvalidObjectException, TelegramApiException {

		String po = message.getText();

		session.get(message.getFrom().getId()).setNewOrder(new Order());
		session.get(message.getFrom().getId()).setOrder(new Order(po));
		session.get(message.getFrom().getId()).getNewOrder().setChatId(message.getFrom().getId());
		updateUserStatus(message.getFrom().getId(), REORDER_2_ASK_ORDER_ID);

		SendMessage sendMsg = new SendMessage();
		
		sendMsg.setChatId(message.getChatId());

		List<POType> types = null;
//		types = service.getPOTypes(Constant.GET_REORDER_PO, po, ""+message.getChatId());
		
		if (types != null && types.size() > 0) {
			String formatedString = LocalisationService.getInstance().getString("pilihPOType", language);
			sendMsg.setText(formatedString);
			sendMsg.setReplyMarkup(getPOTypeKeyboard(types));
		} else {
			sendMsg.setText("PO yang Anda masukkan " + po +" tidak ditemukan. Coba kirimkan kembali nomor PO yang mau direorder.");
			updateUserStatus(message.getFrom().getId(), REORDER_1_ASK_PO_NO);
		}

		execute(sendMsg);
		
		try {
			ReorderChat chat =  (ReorderChat) session.get(message.getFrom().getId()).getChat().get(Constant.REORDER_CHAT);
//			chat.setPoNo(po);
			convSvc.updReorderPo(chat);
		} catch(Exception e) {
			log.error(e.getMessage());
		}
	}

	private void onNewIssue(Message message, String language) throws InvalidObjectException, TelegramApiException {
		updateUserStatus(message.getFrom().getId(), ISSUE_1_ASK_PO);

		NLUser user = session.get(message.getFrom().getId()).getUser();
		Issue issue = new Issue();
		issue.setMsisdn(user.getMsisdn());
		issue.setCompanyName(user.getCompanyName());
		issue.setUserId(message.getFrom().getId());
		issue.setChatId(message.getChatId());
		issue.setUserName(message.getFrom().getFirstName() + " " + message.getFrom().getLastName());

		session.get(message.getFrom().getId()).setIssue(issue);

		SendMessage sendMsg = new SendMessage();
		sendMsg.setText(LocalisationService.getInstance().getString("keluhan", language));
		sendMsg.setChatId(message.getChatId());
		execute(sendMsg);
		sendMsg.setText(LocalisationService.getInstance().getString("nomorPOComplaint", language));
		sendMsg.setChatId(message.getChatId());
		execute(sendMsg);
		
		try {
			KeluhanChat chat = null;
//			new KeluhanChat(message.getFrom().getId(), message.getText());
			convSvc.newKeluhan(chat);
			session.get(message.getFrom().getId()).getChat().put(Constant.KELUHAN_CHAT, chat);
		} catch(Exception e) {
			log.error(e.getMessage());
		}
	}

	private void chooseTopic(String orderNo, Integer chatId, String language)
			throws InvalidObjectException, TelegramApiException {
		updateUserStatus(chatId, ISSUE_3_ASK_TOPIC);

		session.get(chatId).getIssue().setOrderNo(orderNo);
		session.get(chatId).getIssue().setUserId(chatId);
		SendMessage sendMsg = new SendMessage();
		String formatedString = LocalisationService.getInstance().getString("chooseTopic", language);
		sendMsg.setText(formatedString);
		sendMsg.setChatId("" + chatId);

		sendMsg.setReplyMarkup(getIssueTopicsMenuKeyboard("en"));

		execute(sendMsg);
		
		try { //this save order id
			KeluhanChat chat =  (KeluhanChat) session.get(chatId).getChat().get(Constant.KELUHAN_CHAT);
			chat.setOrderId(orderNo);
			convSvc.updKelOrderId(chat);
		} catch(Exception e) {
			log.error(e.getMessage());
		}
		
	}

	private void askForIssueDetail(Message message, String language)
			throws InvalidObjectException, TelegramApiException {
		updateUserStatus(message.getFrom().getId(), ISSUE_4_ASK_DETAIL);
		String input = message.getText().replaceAll("[^\\x00-\\x7F]", "").trim();
		boolean found = false;
		List<String> topics=service.getIssueTopics();
		if (topics != null) {
			for(String topic: topics) {
				if (topic.replace(topic.substring(0, topic.indexOf(",")+1),"").equalsIgnoreCase(input)) {
					found = true;
					break;
				}
			}
		}
		SendMessage sendMsg = new SendMessage();
		sendMsg.setChatId(message.getChatId());
		if (found) {
			session.get(message.getFrom().getId()).getIssue().setTopic(input);
			String formatedString = "";
			formatedString = String.format(LocalisationService.getInstance().getString("issueDetails", language),
					message.getText());
			sendMsg.setText(formatedString);
			sendMsg.setReplyMarkup(getHideKeyboard());
			execute(sendMsg);
		} else {
			sendMsg.setText("Topik yang Anda pilih tidak ada, mohon pilih topik dari pilihan yang ada.");
			execute(sendMsg);
			chooseTopic(session.get(message.getFrom().getId()).getIssue().getOrderNo(), message.getFrom().getId(), language);
		}

		try {
			KeluhanChat chat =  (KeluhanChat) session.get(message.getFrom().getId()).getChat().get(Constant.KELUHAN_CHAT);
			chat.setTypeKeluhan(input);
			convSvc.updKelType(chat);
		} catch(Exception e) {
			log.error(e.getMessage());
		}
		
	}

	private void onSolar1(Message message, String language) throws InvalidObjectException, TelegramApiException {
		updateUserStatus(message.getFrom().getId(), SOLAR_STATUS);
		SendMessage sendMsg = new SendMessage();
		sendMsg.setText(LocalisationService.getInstance().getString("solar1", language));
		sendMsg.setChatId(message.getChatId());
		execute(sendMsg);
	}
	private void onSolar2(Message message, String language) throws InvalidObjectException, TelegramApiException {
		updateUserStatus(message.getFrom().getId(), SOLAR_STATUS_ANSWER);
		SendMessage sendMsg = new SendMessage();
		sendMsg.setText(message.getText() + " sudah disimpan\n" + LocalisationService.getInstance().getString("solar2", language));
		sendMsg.setChatId(message.getChatId());
		sendMsg.setReplyMarkup(getOptionButtons(true, STRING_YES, STRING_NO));
		execute(sendMsg);
	}
	
	private void onSolar3(CallbackQuery message, String language) throws InvalidObjectException, TelegramApiException {
		
		if (message.getData().equals(STRING_YES)) {
			updateUserStatus(message.getFrom().getId(), SOLAR_STATUS);
			SendMessage sendMsg = new SendMessage();
			sendMsg.setText(LocalisationService.getInstance().getString("solar1", language));
			sendMsg.setChatId(message.getMessage().getChatId());
			execute(sendMsg);
		} else {
			updateUserStatus(message.getFrom().getId(), 0);
		}
		
	}

	private void finish(User user, Message message, String language)
			throws InvalidObjectException, TelegramApiException {
		resetUserStatus(user.getId());
		Issue issue = session.get(user.getId()).getIssue();

		SendMessage sendMessageRequest = new SendMessage();
		sendMessageRequest.setChatId(message.getChatId());

		ObjectMapper mapper = new ObjectMapper();
		String str = "";

		try {
			str = mapper.writeValueAsString(issue);
			log.debug("Issue:" + str);
			if (str != null) {
				MQUtil.getInstance().postIssueToMq(str);
				sendMessageRequest.setText(LocalisationService.getInstance().getString("finish", language));
			} else {
				sendMessageRequest.setText(
						"Maaf keluhan yang Anda sampaikan tidak bisa kami terima dengan baik, mohon bisa diulangi.");

			}
			execute(sendMessageRequest);
			session.get(user.getId()).setIssue(null);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private boolean checkUserByChatId(Integer chatId, String language)
			throws InvalidObjectException, TelegramApiException {
		NLUser user = service.getUserByChatId(chatId);
		SessionHolder holder = (SessionHolder) session.get(chatId);
		
		if (holder != null) {
			if (user == null) {
				session.remove(chatId);
				return false;
			} else { 
				return true;
			}
		} else {
			if (user != null) {
				session.put(chatId, new SessionHolder(user));
				return true;
			} else {
				return false;
			}
		}

	}

	

	private void onOtherPhotoReceived(Update update, String language)
			throws InvalidObjectException, TelegramApiException {
		// SendMessage sendMessageRequest = new SendMessage();
		BotApiObject photoSize = getPhoto(update);
		String filePath = TELEGRAM_URL + BotConfig.NL_CS_TOKEN + "/" + getFilePath(photoSize);

		session.get(update.getMessage().getFrom().getId()).getIssue().setAttachment3(filePath);
		if (update.getMessage().getCaption() != null && !update.getMessage().getCaption().trim().equals(""))
			session.get(update.getMessage().getFrom().getId()).getIssue().setAtt3Detail(update.getMessage().getCaption());
		else
			session.get(update.getMessage().getFrom().getId()).getIssue().setAtt3Detail("Picture 3");
		finish(update.getMessage().getFrom(), update.getMessage(), language);

	}

	private void onPhotoReceived(Update update, String language, Integer imageStatus)
			throws InvalidObjectException, TelegramApiException {
		updateUserStatus(update.getCallbackQuery().getFrom().getId(), imageStatus);
		
		SendMessage sendMessageRequest = new SendMessage();

		sendMessageRequest.setText(LocalisationService.getInstance().getString("otherImageQuestion", language));
		sendMessageRequest.setChatId(update.getMessage().getChatId());
		sendMessageRequest.setReplyMarkup(getOptionButtons(true, STRING_FINISH));
		execute(sendMessageRequest);
		
		BotApiObject botObj = getPhoto(update);
		String filePath = TELEGRAM_URL + BotConfig.NL_CS_TOKEN + "/" + getFilePath(botObj);
		
		// java.io.File theFile = downloadTheFile(filePath);
		if (imageStatus == ISSUE_6_SECOND_IMAGE) {
			session.get(update.getMessage().getFrom().getId()).getIssue().setAttachment1(filePath);
			if (update.getMessage().getCaption() != null && !update.getMessage().getCaption().trim().equals(""))
				session.get(update.getMessage().getFrom().getId()).getIssue().setAtt1Detail("Ket. gambar: " + update.getMessage().getCaption());
		} if (imageStatus == ISSUE_7_THIRD_IMAGE) {
			session.get(update.getMessage().getFrom().getId()).getIssue().setAttachment2(filePath);
			if (update.getMessage().getCaption() != null && !update.getMessage().getCaption().trim().equals(""))
				session.get(update.getMessage().getFrom().getId()).getIssue().setAtt2Detail(update.getMessage().getCaption());
			else
				session.get(update.getMessage().getFrom().getId()).getIssue().setAtt2Detail("Picture 2");
		}
	}

	private void askFirstImage(Message message, String language) throws InvalidObjectException, TelegramApiException {
		updateUserStatus(message.getFrom().getId(), ISSUE_5_FIRST_IMAGE);

		// service.updateIssueDetail(issueNo, message.getText());
		session.get(message.getFrom().getId()).getIssue().setDetail(message.getText());

		SendMessage sendMessageRequest = new SendMessage();
		sendMessageRequest.setText(LocalisationService.getInstance().getString("firstImageQuestion", language));
		sendMessageRequest.setChatId(message.getChatId());
//		sendMessageRequest.setReplyMarkup(getFinishKeyboard());
		sendMessageRequest.setReplyMarkup(getOptionButtons(true, STRING_FINISH));
		execute(sendMessageRequest);
		
		try {
			KeluhanChat chat =  (KeluhanChat) session.get(message.getFrom().getId()).getChat().get(Constant.KELUHAN_CHAT);
			chat.setDetailKeluhan(message.getText());
			convSvc.updKelDetail(chat);
		} catch(Exception e) {
			log.error(e.getMessage());
		}
	}
	
	private void askForImageNotText(Message message, String language) throws InvalidObjectException, TelegramApiException {
		SendMessage sendMessageRequest = new SendMessage();
		sendMessageRequest.setText(LocalisationService.getInstance().getString("imageNotText", language));
		sendMessageRequest.setChatId(message.getChatId());
//		sendMessageRequest.setReplyMarkup(getFinishKeyboard());
		sendMessageRequest.setReplyMarkup(getOptionButtons(true, STRING_FINISH));
		execute(sendMessageRequest);
	}

	/*
	 * private void onBroadcastDetailReceived(Message message, String language)
	 * throws InvalidObjectException, TelegramApiException { List<Customer>
	 * users = service.getUsers(); resetUserStatus(message.getFrom().getId());
	 * StringBuilder sb = new
	 * StringBuilder(LocalisationService.getInstance().getString(
	 * "broadcastHeader", language)); sb.append(message.getText());
	 * 
	 * if (users != null && users.size() > 0) { for (Customer user : users) {
	 * SendMessage sendMessageRequest = new SendMessage();
	 * sendMessageRequest.setText(sb.toString());
	 * sendMessageRequest.setChatId(user.getUserId().longValue());
	 * execute(sendMessageRequest); } } }
	 */

	private void cannotUnderstand(Message message, String language)
			throws InvalidObjectException, TelegramApiException {
		SendMessage sendMessageRequest = new SendMessage();
		sendMessageRequest.setText(LocalisationService.getInstance().getString("cannotUnderstand", language));
		sendMessageRequest.setChatId(message.getChatId());
		execute(sendMessageRequest);
	}

	/*
	 * private void onYesforPhotoX(Message message, String language, Integer
	 * imageStatus) throws InvalidObjectException, TelegramApiException {
	 * updateUserStatus(message.getChat().getId().intValue(), imageStatus);
	 * SendMessage sendMessageRequest = new SendMessage();
	 * sendMessageRequest.setText(LocalisationService.getInstance().getString(
	 * "selectImage", language));
	 * sendMessageRequest.setChatId(message.getChatId());
	 * execute(sendMessageRequest); }
	 */

	private void sendNotRegisteredMessage(Message message, String language)
			throws InvalidObjectException, TelegramApiException {
		SendMessage sendMessageRequest = new SendMessage();
		String formatedString = String.format(LocalisationService.getInstance().getString("notRegistered", language),
				message.getFrom().getFirstName());
		sendMessageRequest.setText(formatedString);
		sendMessageRequest.setChatId(message.getChatId());

		sendMessageRequest.setReplyMarkup(getHideKeyboard());

		execute(sendMessageRequest);
		sendMessageRequest.setText(LocalisationService.getInstance().getString("notRegistered2"));
		execute(sendMessageRequest);
	}

	private void sendHelpMessage(Message message, String language) throws InvalidObjectException, TelegramApiException {
		updateUserStatus(message.getFrom().getId(), CEK_ORDER_1_ASK_PO_NO);
		SendMessage sendMessageRequest = new SendMessage();
		String formatedString = String.format(LocalisationService.getInstance().getString("helpFiles", language),
				Commands.sandarCommand, Commands.reorderCommand, Commands.keluhanCommand);
		sendMessageRequest.setText(formatedString);
		sendMessageRequest.setChatId(message.getChatId());

		sendMessageRequest.setReplyMarkup(getHideKeyboard());

		execute(sendMessageRequest);
	}

	private static ReplyKeyboardMarkup getPhoneNoKeyboard() {
		ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
		List<KeyboardRow> keyboard = new ArrayList<>();
		KeyboardRow row = new KeyboardRow();
		KeyboardButton msisdn = new KeyboardButton("Kirimkan No HP");
		msisdn.setRequestContact(true);
		row.add(msisdn);
		keyboard.add(row);
		markup.setKeyboard(keyboard);

		return markup;
	}

	private static InlineKeyboardMarkup getROButton(String orderId, boolean reOrder, boolean isFirst) {
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
		List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
		List<InlineKeyboardButton> row = new ArrayList<>();

		InlineKeyboardButton btn = new InlineKeyboardButton();
		btn.setText(reOrder ? "Reorder" : "Keluhkan");
		btn.setCallbackData(orderId);
		row.add(btn);

		if (!isFirst) {
			btn = new InlineKeyboardButton();
			btn.setText("Cukup itu saja!");
			btn.setCallbackData(STRING_FINISH);
			row.add(btn);
		}

		keyboard.add(row);
		markup.setKeyboard(keyboard);

		return markup;
	}

	private static InlineKeyboardMarkup getPOTypeKeyboard(List<POType> types) {
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
		List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
		List<InlineKeyboardButton> row = new ArrayList<>();

		for (POType t : types) {
			InlineKeyboardButton btn = new InlineKeyboardButton();
			btn.setText(t.getName());
			btn.setCallbackData(t.getName());
			row.add(btn);
		}

		keyboard.add(row);
		markup.setKeyboard(keyboard);

		return markup;
	}

	/*private static InlineKeyboardMarkup getFinishKeyboard() {
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
		List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
		List<InlineKeyboardButton> row = new ArrayList<>();
		
		 * InlineKeyboardButton yesButton = new InlineKeyboardButton();
		 * yesButton.setText(STRING_YES); yesButton.setCallbackData(STRING_YES);
		 
		InlineKeyboardButton noButton = new InlineKeyboardButton();
		noButton.setText(STRING_FINISH);
		noButton.setCallbackData(STRING_FINISH);

		// row.add(yesButton);
		row.add(noButton);
		keyboard.add(row);
		markup.setKeyboard(keyboard);

		return markup;
	}*/
	
	
	
	/*private static InlineKeyboardMarkup getYesNoKeyboard() {
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
		List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
		List<InlineKeyboardButton> row = new ArrayList<>();
		InlineKeyboardButton yesButton = new InlineKeyboardButton();
		yesButton.setText(STRING_YES);
		yesButton.setCallbackData(STRING_YES);
		InlineKeyboardButton noButton = new InlineKeyboardButton();
		noButton.setText(STRING_NO);
		noButton.setCallbackData(STRING_NO);

		row.add(yesButton);
		row.add(noButton);
		keyboard.add(row);
		markup.setKeyboard(keyboard);

		return markup;
	}*/

	private static ReplyKeyboardRemove getHideKeyboard() {
		ReplyKeyboardRemove replyKeyboardHide = new ReplyKeyboardRemove();
		replyKeyboardHide.setSelective(false);
		return replyKeyboardHide;
	}

	/*
	 * private static ReplyKeyboardMarkup getSubTopicMenuKeyboard(String topic,
	 * String language) { ReplyKeyboardMarkup replyKeyboardMarkup = new
	 * ReplyKeyboardMarkup(); replyKeyboardMarkup.setSelective(true);
	 * replyKeyboardMarkup.setResizeKeyboard(true); //
	 * replyKeyboardMarkup.setOneTimeKeyboad(false);
	 * 
	 * List<String> topics = NLBotService.getInstance().getDefectList(topic);
	 * int topicCount = topics.size(); int cols = 2; int rows = (topicCount %
	 * cols) > 0 ? (topicCount / cols) + 1 : topicCount / cols; int topicNo = 0;
	 * 
	 * List<KeyboardRow> keyboard = new ArrayList<>(); KeyboardRow[] keyboardRow
	 * = new KeyboardRow[rows]; for (int i = 0; i < rows; i++) { keyboardRow[i]
	 * = new KeyboardRow(); for (int j = 0; j < cols && topicNo < topicCount;
	 * j++) { keyboardRow[i].add(topics.get(topicNo)); topicNo++; }
	 * keyboard.add(keyboardRow[i]); }
	 * replyKeyboardMarkup.setKeyboard(keyboard);
	 * 
	 * return replyKeyboardMarkup; }
	 */
	
	private static InlineKeyboardMarkup getOptionButtons(boolean isOneRow, String... options) {
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
		List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
		List<InlineKeyboardButton> row = null;
		
		InlineKeyboardButton[] btn = new InlineKeyboardButton[options.length];;
		if (isOneRow) {
			row = new ArrayList<>();
			for (int i=0; i < options.length; i++) {
				btn[i] = new InlineKeyboardButton();
				btn[i].setText(options[i]);
				btn[i].setCallbackData(options[i]);
				row.add(btn[i]);
			}
			keyboard.add(row);
		} else {
			for (int i=0; i < options.length; i++) {
				row = new ArrayList<>();
				btn[i] = new InlineKeyboardButton();
				btn[i].setText(options[i]);
				btn[i].setCallbackData(options[i]);
				row.add(btn[i]);
				keyboard.add(i, row);
			}
		}
		
		markup.setKeyboard(keyboard);
		return markup;
	}

	private static ReplyKeyboardMarkup getIssueTopicsMenuKeyboard(String language) {
		ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
		replyKeyboardMarkup.setSelective(true);
		replyKeyboardMarkup.setResizeKeyboard(true);
		// replyKeyboardMarkup.setOneTimeKeyboad(false);

		List<String> topics = NLBotService.getInstance().getIssueTopics();
		int topicCount = topics.size();
		int cols = 1;
		int rows = (topicCount % cols) > 0 ? (topicCount / cols) + 1 : topicCount / cols;
		int topicNo = 0;
		String[] t = null;

		List<KeyboardRow> keyboard = new ArrayList<>();
		KeyboardRow[] keyboardRow = new KeyboardRow[rows];
		for (int i = 0; i < rows; i++) {
			keyboardRow[i] = new KeyboardRow();
			for (int j = 0; j < cols && topicNo < topicCount; j++) {
				t = topics.get(topicNo).split(",");
				if (t != null && t.length == 2 && !t[0].equals("")) {
					keyboardRow[i].add(Emoji.valueOf(t[0]).toString() + " " + t[1]);
				} else {
					keyboardRow[i].add(t[1]);
				}
				topicNo++;
			}
			keyboard.add(keyboardRow[i]);
		}
		replyKeyboardMarkup.setKeyboard(keyboard);

		return replyKeyboardMarkup;
	}

	/*
	 * private static ReplyKeyboardMarkup getStoreLocationMenuKeyboard(String
	 * language) { ReplyKeyboardMarkup replyKeyboardMarkup = new
	 * ReplyKeyboardMarkup(); replyKeyboardMarkup.setSelective(true);
	 * replyKeyboardMarkup.setResizeKeyboard(true); //
	 * replyKeyboardMarkup.setOneTimeKeyboad(false);
	 * 
	 * // List<Store> stores = NLBotService.getInstance().getStoreLocations();
	 * int storeCount = 0;// stores.size(); int cols = 4; int rows = (storeCount
	 * % cols) > 0 ? (storeCount / cols) + 1 : storeCount / cols; int storeNo =
	 * 0;
	 * 
	 * List<KeyboardRow> keyboard = new ArrayList<>(); KeyboardRow[] keyboardRow
	 * = new KeyboardRow[rows]; for (int i = 0; i < rows; i++) { keyboardRow[i]
	 * = new KeyboardRow(); for (int j = 0; j < cols && storeNo < storeCount;
	 * j++) { // keyboardRow[i].add(stores.get(storeNo).getShortName());
	 * storeNo++; } keyboard.add(keyboardRow[i]); }
	 * replyKeyboardMarkup.setKeyboard(keyboard);
	 * 
	 * return replyKeyboardMarkup; }
	 */

	/*
	 * private static String getStoreEmoji(String store, String language) {
	 * return String.format(store, Emoji.STATION.toString()); }
	 */

	private static BotApiObject getPhoto(Update update) {
		// Check that the update contains a message and the message has a photo
		if (update.hasMessage() && update.getMessage().hasPhoto()) {
			// When receiving a photo, you usually get different sizes of it
			List<PhotoSize> photos = update.getMessage().getPhoto();
			
			// We fetch the bigger photo
			return photos.stream().sorted(Comparator.comparing(PhotoSize::getFileSize).reversed()).findFirst()
					.orElse(null);
		} else if (update.hasMessage() && update.getMessage().hasDocument()) {
			Document doc = update.getMessage().getDocument();
			return doc;
		}

		// Return null if not found
		return null;
	}

	private String getFilePath(BotApiObject obj) {
		
//		Objects.requireNonNull(obj, "Kirimkan sebuah photo atau dokumen");
		PhotoSize photo = null;
		Document doc = null;
		if (obj instanceof PhotoSize) {
			photo = (PhotoSize) obj;

			if (photo.hasFilePath()) { // If the file_path is already present, we are done!
				return photo.getFilePath();
			} else {
				GetFile getFileMethod = new GetFile();
		        getFileMethod.setFileId(photo.getFileId());
		        try {
		            // We execute the method using AbsSender::getFile method.
		            File file = execute(getFileMethod);
		            // We now have the file_path
		            return file.getFilePath();
		        } catch (TelegramApiException e) {
		            e.printStackTrace();
		        }
			}
		} else if (obj instanceof Document){ // If not, let find it
			// We create a GetFile method and set the file_id from the photo
			doc = (Document) obj;
			GetFile getFileMethod = new GetFile();
			getFileMethod.setFileId(doc.getFileId());
			try {
				// We execute the method using AbsSender::getFile method.
				// File file = getFile(getFileMethod);

				File file = (File) execute(getFileMethod);

				// We now have the file_path
				return file.getFilePath();
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
		} 
		return null; // Just in case
	}

	private void updateUserStatus(Integer userId, Integer status) {
		session.get(userId).setStatus(status);
	}

	private void resetUserStatus(Integer userId) {
		session.get(userId).setStatus(0);
	}

	private Integer getUserStatus(Integer userId) {
		return session.get(userId).getStatus();
	}
}
