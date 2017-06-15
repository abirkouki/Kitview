package util.sound;

import android.content.Context;
import android.media.MediaPlayer;

public class SoundUtil {

	private static MediaPlayer mMediaPlayer = null;
	private static Context _context;
	public static void playSound(Context context,int rawResourceId, boolean soundEnabled){
		try{
			if(soundEnabled && context != null && rawResourceId != 0){
				_context = context;
				mMediaPlayer = MediaPlayer.create(_context, rawResourceId);

				if(mMediaPlayer != null){
					mMediaPlayer.setVolume(1.0f, 1.0f);	
					mMediaPlayer.start();
					mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
						@Override
						public void onCompletion(MediaPlayer mp) {
							mMediaPlayer.stop();
							mMediaPlayer.release();
						}
					});
				}
			}
			//Resource not found
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void destroy(){
		if(mMediaPlayer != null){
			try{
				mMediaPlayer.stop();

			}catch(Exception e){
				e.printStackTrace();
			}

			mMediaPlayer.release();
			mMediaPlayer.setOnCompletionListener(null);
			_context = null;
			mMediaPlayer = null;


		}
	}
}