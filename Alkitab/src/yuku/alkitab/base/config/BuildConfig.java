package yuku.alkitab.base.config;

import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import yuku.alkitab.base.ac.VersionsActivity.MVersionPreset;
import yuku.alkitab.base.storage.Db;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;

public class BuildConfig {
	public static final String TAG = BuildConfig.class.getSimpleName();

	public String internalPrefix;
	public String internalShortName;
	public String internalLongName;
	public boolean menuDevotion;
	public boolean menuGebug;
	public boolean menuVersions;
	public boolean menuHelp;
	public boolean menuDonation;
	public boolean menuSongs;
	public List<MVersionPreset> presets;
	public String url_prefix;
	public String url_format;
	public String[] url_namaKitabStandar;

	
	private static BuildConfig lastConfig;
	private static String lastPackageName;
	
	private BuildConfig() {}
	
	public static BuildConfig get(Context context) {
		String packageName = context.getPackageName();
		if (packageName.equals(lastPackageName)) {
			return lastConfig;
		}
		
		int resId;
		if (packageName.equals("yuku.alkitab.beta")) { // special for yuku.alkitab.beta //$NON-NLS-1$
			resId = context.getResources().getIdentifier("build_config_yuku_alkitab", "xml", packageName); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			resId = context.getResources().getIdentifier("build_config_" + packageName.replace('.', '_'), "xml", packageName); //$NON-NLS-1$ //$NON-NLS-2$
			if (resId == 0) {
				return null;
			}
		}
		
		BuildConfig res = null;
		try {
			res = loadConfig(context, context.getResources().getXml(resId));
			lastConfig = res;
			lastPackageName = packageName;
		} catch (Exception e) {
			Log.e(TAG, "error in loading build config", e); //$NON-NLS-1$
		}
		
		return res;
	}

	private static BuildConfig loadConfig(Context context, XmlResourceParser parser) throws Exception {
		BuildConfig res = new BuildConfig();
		
		List<MVersionPreset> xpreset = new ArrayList<MVersionPreset>();
		int urutanPreset = 10;

		while (true) {
			int next = parser.next();
			if (next == XmlPullParser.START_TAG && "menu".equals(parser.getName())) { //$NON-NLS-1$
				res.menuHelp = parser.getAttributeBooleanValue(null, "bantuan", false); //$NON-NLS-1$
				res.menuDonation = parser.getAttributeBooleanValue(null, "donasi", false); //$NON-NLS-1$
				res.menuVersions = parser.getAttributeBooleanValue(null, "edisi", false); //$NON-NLS-1$
				res.menuGebug = parser.getAttributeBooleanValue(null, "gebug", false); //$NON-NLS-1$
				res.menuDevotion = parser.getAttributeBooleanValue(null, "renungan", false); //$NON-NLS-1$
				res.menuSongs = parser.getAttributeBooleanValue(null, "songs", false); //$NON-NLS-1$
			} else if (next == XmlPullParser.START_TAG && "internal".equals(parser.getName())) { //$NON-NLS-1$
				res.internalShortName = parser.getAttributeValue(null, "shortName"); //$NON-NLS-1$
				res.internalLongName = parser.getAttributeValue(null, "longName"); //$NON-NLS-1$
				res.internalPrefix = parser.getAttributeValue(null, "prefix"); //$NON-NLS-1$
			} else if (next == XmlPullParser.START_TAG && "preset".equals(parser.getName())) { //$NON-NLS-1$
				MVersionPreset preset = new MVersionPreset();
				preset.type = Db.Edisi.jenis_preset;
				preset.shortName = parser.getAttributeValue(null, "shortName"); //$NON-NLS-1$
				preset.longName = parser.getAttributeValue(null, "longName"); //$NON-NLS-1$
				preset.presetFilename = parser.getAttributeValue(null, "namafile_preset"); //$NON-NLS-1$
				preset.url = parser.getAttributeValue(null, "url"); //$NON-NLS-1$
				preset.ordering = ++urutanPreset;
				preset.locale = parser.getAttributeValue(null, "locale"); //$NON-NLS-1$
				xpreset.add(preset);
			} else if (next == XmlPullParser.START_TAG && "url".equals(parser.getName())) { //$NON-NLS-1$
				// TODO support more url's if needed. now only one.
				res.url_prefix = parser.getAttributeValue(null, "prefix"); //$NON-NLS-1$
				res.url_format = parser.getAttributeValue(null, "format"); //$NON-NLS-1$
				res.url_namaKitabStandar = context.getResources().getStringArray(context.getResources().getIdentifier("nama_kitab_standar_" + parser.getAttributeValue(null, "lang"), "array", context.getPackageName())); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				if (res.url_prefix == null || res.url_format == null || res.url_namaKitabStandar == null) {
					throw new RuntimeException("wrong share url config!"); //$NON-NLS-1$
				}
			} else if (next == XmlPullParser.END_DOCUMENT) {
				break;
			}
		}
		
		res.presets = xpreset;
		
		return res;
	}
}
