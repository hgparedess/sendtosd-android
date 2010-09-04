package ru.gelin.android.sendtosd;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

/**
 *  Utilities to manipulate intents.
 */
public class IntentUtils implements Constants {
    
    private static final String TEXT_FILE_NAME = "text.txt";
    
    /** Current context */
    Context context;
    /** Processing intent */
    Intent intent;
    
    /**
     *  Creates the utils.
     *  @param  context current context
     *  @param  intent  intent to process
     */
    public IntentUtils(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
    }
    
    /**
     *  Tries to guess the filename from the intent.
     */
    public String getFileName() {
        if (isTextIntent()) {
            return TEXT_FILE_NAME;
        }
        Uri uri = getStreamUri();
        String fileName = uri.getLastPathSegment();
        if (fileName.contains(".")) {   //has extension
            return fileName;
        }
        String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(intent.getType());
        if (extension != null) {
            return fileName + "." + extension;
        }
        return fileName;
    }
    
    /**
     *  Returns true if the intent is plain/text intent.
     */
    public boolean isTextIntent() {
        return "text/plain".equals(intent.getType());
    }
    
    /**
     *  Returns the Uri of the stream of the intent.
     */
    public Uri getStreamUri() {
        return (Uri)intent.getExtras().get(Intent.EXTRA_STREAM);
    }
    
    /**
     *  Returns path on external storage provided with the intent.
     */
    public File getPath() {
        String path = intent.getStringExtra(EXTRA_PATH);
        if (path == null) {
            return Environment.getExternalStorageDirectory();
        }
        try {
            return new File(path).getCanonicalFile();
        } catch (IOException e) {
            return new File(path);
        }
    }
    
    /**
     *  Returns the file as stream.
     */
    public InputStream getFileStream() throws FileNotFoundException {
        if (isTextIntent()) {
            String text = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (text == null) {
                text = "";
            }
            return new ByteArrayInputStream(text.getBytes());
        }
        Uri uri = getStreamUri();
        return context.getContentResolver().openInputStream(uri);
    }
    
    /**
     *  Logs debug information about the intent.
     */
    void logIntentInfo() {
        Log.i(TAG, "intent: " + intent);
        return;
        /*
        Log.d(TAG, "data: " + intent.getData());
        Bundle extras = intent.getExtras();
        if (extras != null) {
            for (String key : extras.keySet()) {
                Log.d(TAG, "extra: " + key + " = " + extras.get(key));
            }
        }
        */
    }

}