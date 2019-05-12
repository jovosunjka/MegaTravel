using LogSimulator.Service.Interface;
using System;
using System.Configuration;
using System.IO;
using System.Threading;

namespace LogSimulator.Service
{
    public class AppSettings : IAppSettings
    {
        private readonly Mutex _mutex;

        public AppSettings()
        {
            _mutex = new Mutex();
        }

        public string AlarmPossibility => Execute(() => ConfigurationManager.AppSettings["AlarmPossibility"]);

        public string SleepMilliseconds => Execute(() => ConfigurationManager.AppSettings["SleepMilliseconds"]);

        public string AntivirusThreatsNum => Execute(() => ConfigurationManager.AppSettings["AntivirusThreatsNum"]);

        public string IpAddress1 => Execute(() => ConfigurationManager.AppSettings["IpAddress1"]);

        public string IpAddress2 => Execute(() => ConfigurationManager.AppSettings["IpAddress2"]);

        public string IpAddress3 => Execute(() => ConfigurationManager.AppSettings["IpAddress3"]);

        public string MaliciousIpAddress => Execute(() => ConfigurationManager.AppSettings["MaliciousIpAddress"]);

        public string UnsuccessfulLoginAttemptNum => Execute(() => ConfigurationManager.AppSettings["UnsuccessfulLoginAttemptNum"]);

        public string Username => Execute(() => ConfigurationManager.AppSettings["Username"]);

        public string MaliciousUsername => Execute(() => ConfigurationManager.AppSettings["MaliciousUsername"]);

        public string AttackRequestNum => Execute(() => ConfigurationManager.AppSettings["AttackRequestNum"]);

        public string AntivirusLogsFolderPath => Execute(() => Path.Combine(ConfigurationManager.AppSettings["AntivirusLogsFolderPath"],
                                                        $"Logs_{DateTime.UtcNow.ToString(ConfigurationManager.AppSettings["DateFormat"])}.txt"));

        public string LoginLogsFolderPath => Execute(() => Path.Combine(ConfigurationManager.AppSettings["LoginLogsFolderPath"],
                                                        $"Logs_{DateTime.UtcNow.ToString(ConfigurationManager.AppSettings["DateFormat"])}.txt"));

        public string OtherLogsFolderPath => Execute(() => Path.Combine(ConfigurationManager.AppSettings["OtherLogsFolderPath"],
                                                        $"Logs_{DateTime.UtcNow.ToString(ConfigurationManager.AppSettings["DateFormat"])}.txt"));

        public string Username2 => Execute(() => ConfigurationManager.AppSettings["Username2"]);

        public string LogSequencerCurrentValue
        {
            get { return Execute(() => ConfigurationManager.AppSettings["LogSequencerCurrentValue"]); }
            set { UpdateValue("LogSequencerCurrentValue", value); }
        }

        public string AntivirusThreatSequencerCurrentValue
        {
            get { return Execute(() => ConfigurationManager.AppSettings["AntivirusThreatSequencerCurrentValue"]); }
            set { UpdateValue("AntivirusThreatSequencerCurrentValue", value); }
        }

        private string Execute(Func<string> func)
        {
            lock (_mutex)
            {
                ConfigurationManager.RefreshSection("appSettings");
                return func.Invoke();
            }
        }

        private void UpdateValue(string field, string value)
        {
            lock (_mutex)
            {
                var configuration = ConfigurationManager.OpenExeConfiguration(System.Reflection.Assembly.GetExecutingAssembly().Location);
                configuration.AppSettings.Settings[field].Value = value;
                configuration.Save();
            }
        }
    }
}
