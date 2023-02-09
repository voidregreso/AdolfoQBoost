#ifdef __cplusplus
extern "C" {
#endif

void SendEventAsync(const char* trackingId, const char* category, const char* action);
void SendEvent(const char* trackingId, const char* category, const char* action);

#ifdef __cplusplus
}
#endif