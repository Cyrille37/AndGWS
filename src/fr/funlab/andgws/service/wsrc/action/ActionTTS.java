package fr.funlab.andgws.service.wsrc.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

import fr.funlab.util.Crypto;
import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.TextToSpeech.EngineInfo;
import android.util.Log;

public class ActionTTS extends UtteranceProgressListener implements
		TextToSpeech.OnInitListener {

	public interface ActionTTSListener {

		public void onInit(String uid);

		public void onStart(String uid);

		public void onDone(String uid);

		public void onError(String uid);
	}

	class StringEntry {
		String str;
		ActionTTSListener actionTTSListener;
		String utteranceId;

		public StringEntry(String str, ActionTTSListener actionTTSListener,
				String utteranceId) {
			this.str = str;
			this.actionTTSListener = actionTTSListener;
			this.utteranceId = utteranceId;
		}
	}

	Context context;
	TextToSpeech tts = null;
	List<StringEntry> stringsSync;
	ConcurrentMap<String, StringEntry> stringsPlayingSync;
	AtomicBoolean ttsInitialized = new AtomicBoolean(false);

	public ActionTTS(Context context) {
		super();
		final String LOG_TAG = "ActionTTS.ActionTTS()";

		Log.d(LOG_TAG, "construct a new ActionTTS");

		stringsSync = Collections
				.synchronizedList(new ArrayList<StringEntry>());
		stringsPlayingSync = new ConcurrentHashMap<String, StringEntry>();

		this.context = context;
	}

	public void speak(String str, ActionTTSListener actionTTSListener) {

		final String LOG_TAG = "ActionTTS.speak()";
		Log.d(LOG_TAG, "handle speak() call");

		String utteranceId = Crypto
				.SHA1(str + "|" + System.currentTimeMillis());

		stringsSync.add(new StringEntry(str, actionTTSListener, utteranceId));

		if (tts == null) {
			tts = new TextToSpeech(this.context, this);
		}

		if (actionTTSListener != null)
			actionTTSListener.onInit(utteranceId);

		_speak();
	}

	void _speak() {

		final String LOG_TAG = "ActionTTS._speak()";
		Log.d(LOG_TAG, "managing speak strings");

		if (!ttsInitialized.get()) {
			Log.d(LOG_TAG, "tts not initialized");
			return;
		}

		/*
		 * is TRUE while settings Language, so do not use this information. if
		 * (tts.isSpeaking()) { Log.d(LOG_TAG,"already speaking"); return; }
		 */

		if (stringsSync.size() == 0) {
			Log.d(LOG_TAG, "strings empty");
			return;
		}

		StringEntry se = stringsSync.remove(0);

		stringsPlayingSync.put(se.utteranceId, se);

		HashMap<String, String> map = new HashMap<String, String>();
		map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, se.utteranceId);
		tts.speak(se.str, TextToSpeech.QUEUE_ADD, map);

	}

	/**
	 * implements TextToSpeech.OnInitListener
	 */
	@Override
	public void onInit(int status) {

		final String LOG_TAG = "TextToSpeech.OnInitListener.onInit()";

		Log.d(LOG_TAG, "status=" + status);
		if (status == TextToSpeech.ERROR) {
			Log.e(LOG_TAG, "TextToSpeech.ERROR");
			tts = null;
			return;
		}

		for (EngineInfo ei : tts.getEngines()) {
			Log.d(LOG_TAG, "engine name=" + ei.name);
		}
		if (tts.isLanguageAvailable(Locale.FRANCE) == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
			tts.setLanguage(Locale.FRANCE);
			Log.d(LOG_TAG, "Locale.FRANCE found");
		} else {
			Log.d(LOG_TAG, "Locale.FRANCE NOT found !");
		}

		// >= API 18
		// Log.i(LOG_TAG,
		// "MaxSpeechInputLength: "+tts.getMaxSpeechInputLength());

		tts.setOnUtteranceProgressListener(this);

		this.ttsInitialized.set(true);

		_speak();
	}

	/**
	 * implements UtteranceProgressListener
	 * 
	 * @param utteranceId
	 */
	@Override
	public void onStart(String utteranceId) {
		final String LOG_TAG = "UtteranceProgressListener.onStart()";
		Log.d(LOG_TAG, "start speaking. utteranceId=" + utteranceId);

		StringEntry se = stringsPlayingSync.get(utteranceId);
		if (se.actionTTSListener != null)
			se.actionTTSListener.onStart(utteranceId);
	}

	/**
	 * implements UtteranceProgressListener
	 * 
	 * @param utteranceId
	 */
	@Override
	public void onDone(String utteranceId) {
		final String LOG_TAG = "UtteranceProgressListener.onDone()";
		Log.d(LOG_TAG, "speak done. utteranceId=" + utteranceId);

		StringEntry se = stringsPlayingSync.remove(utteranceId);
		if (se.actionTTSListener != null)
			se.actionTTSListener.onDone(utteranceId);

		_speak();

	}

	/**
	 * implements UtteranceProgressListener
	 * 
	 * @param utteranceId
	 */
	@Override
	public void onError(String utteranceId) {
		final String LOG_TAG = "UtteranceProgressListener.onError()";
		Log.e(LOG_TAG, "speak ERROR ! utteranceId=" + utteranceId);

		StringEntry se = stringsPlayingSync.remove(utteranceId);
		if (se.actionTTSListener != null)
			se.actionTTSListener.onError(utteranceId);
	}

}
