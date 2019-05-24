namespace LogSimulator.Helper
{
    public static class Constants
    {
        public const string State = "State";

        public const string StatesNamespace = "LogSimulator.State";

        public const string NewLine = "\n";

        public static class StateDescription
        {
            public const string NoAlarm = "No alarm";

            public const string LogError = "Log some errors!";

            public const string LoginAttemptWithCommonIp = "Login attempt from common ip";

            public const string LoginWithCommonIp = "Login from common ip";

            public const string AntivirusThreats = "Antivirus threats";

            public const string LoginWithMaliciousIp = "Login attempt from malicious ip";

            public const string DenialOfServiceAttack = "Denial of service attack";

            public const string PaymentSystemAttack = "Payment system attack";

            public const string BruteForceAttack = "Brute force attack";

            public const string ChangeUserInfo = "Change user info";
        }
    }
}
