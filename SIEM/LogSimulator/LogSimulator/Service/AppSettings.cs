using LogSimulator.Service.Interface;
using System;
using System.Configuration;
using System.IO;

namespace LogSimulator.Service
{
    public class AppSettings : IAppSettings
    {
        public string LogsFilePath
        {
            get
            {
                Refresh();
                return Path.Combine(ConfigurationManager.AppSettings["LogFolderPath"], $"Logs_{DateTime.UtcNow.ToShortDateString()}.txt");
            }
        }

        public string AlarmPossibility
        {
            get
            {
                Refresh();
                return ConfigurationManager.AppSettings["AlarmPossibility"];
            }
        }

        public string SleepMilliseconds
        {
            get
            {
                Refresh();
                return ConfigurationManager.AppSettings["SleepMilliseconds"];
            }
        }

        public string AntivirusThreatsNum
        {
            get
            {
                Refresh();
                return ConfigurationManager.AppSettings["AntivirusThreatsNum"];
            }
        }

        public string HostAddress1
        {
            get
            {
                Refresh();
                return ConfigurationManager.AppSettings["HostAddress1"];
            }
        }

        public string HostAddress2
        {
            get
            {
                Refresh();
                return ConfigurationManager.AppSettings["HostAddress2"];
            }
        }

        public string HostAddress3
        {
            get
            {
                Refresh();
                return ConfigurationManager.AppSettings["HostAddress3"];
            }
        }

        public string IpAddress1
        {
            get
            {
                Refresh();
                return ConfigurationManager.AppSettings["IpAddress1"];
            }
        }

        public string IpAddress2
        {
            get
            {
                Refresh();
                return ConfigurationManager.AppSettings["IpAddress2"];
            }
        }

        public string IpAddress3
        {
            get
            {
                Refresh();
                return ConfigurationManager.AppSettings["IpAddress3"];
            }
        }

        public string MaliciousIpAddress
        {
            get
            {
                Refresh();
                return ConfigurationManager.AppSettings["MaliciousIpAddress"];
            }
        }

        public string UnsuccessfulLoginAttemptNum
        {
            get
            {
                Refresh();
                return ConfigurationManager.AppSettings["UnsuccessfulLoginAttemptNum"];
            }
        }

        public string Username
        {
            get
            {
                Refresh();
                return ConfigurationManager.AppSettings["Username"];
            }
        }

        private void Refresh()
        {
            ConfigurationManager.RefreshSection("appSettings");
        }
    }
}
