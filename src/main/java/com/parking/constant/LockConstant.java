package com.parking.constant;

public class LockConstant {

    public static final String LOCK_SHARD_KEY = "lock.shard";
    public static final String LOCK_KEY = "lock:";
    public static final String BREAKDOWN_LOCKS = "breakdown:locks";

    public static class LockError {

        public static final String LOCK_FAILED = "lock failed";
        public static final String UNLOCK_FAILED = "unlock failed";


        public static final Integer LOCK_ERROR_CODE = 80001;
        public static final Integer BREAKDOWN_ERROR_CODE = 80002;
        public static final Integer UNLOCK_ERROR_CODE = 80003;
        public static final Integer LOCK_STATUS_ERROR_CODE = 80004;


    }
}
