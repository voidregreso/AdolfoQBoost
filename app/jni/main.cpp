#include "com_adolfo_QboostService.h"
#include "qspower/power.h"
#include <android/log.h>

int status[8] = { 1, 2, 3, 4, 5, 6, 15};

#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR  , "AdolfoQBoost", __VA_ARGS__)

JNIEXPORT void JNICALL Java_com_adolfo_QboostService_init
(JNIEnv *env, jobject thiz) {
	qspower_init();
}

JNIEXPORT jint JNICALL Java_com_adolfo_QboostService_setMode
(JNIEnv *env, jobject thiz, jint a3, jint a4, jint a5, jint a6) {
	int v6;
	if ( (unsigned int)(a3 - 1) > 6 )
		v6 = 0;
	else
		v6 = status[a3 - 1];
	switch ( a4 ) {
	case 0:
		qspower_request_normal_mode(v6);
		break;
	case 1:
		qspower_request_efficient_mode(0, v6);
		break;
	case 2:
		qspower_request_saver_mode(0, v6);
		break;
	case 3:
		qspower_request_perf_burst_mode(0, v6);
		break;
	case 4:
		qspower_request_window_mode(a5, a6, 0, v6);
		break;
	case 5:
		//qspower_request_super_saver_mode(0, 0, v6);
		LOGE("The mode is abondoned!");
		break;
	default:
		return 0;
	}
	return 0;
}

JNIEXPORT void JNICALL Java_com_adolfo_QboostService_uninit
(JNIEnv *, jobject) {
	qspower_terminate();
}