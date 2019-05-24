using LogSimulator.Model.Enum;
using LogSimulator.Service.Interface;
using System;
using System.Reflection;
using System.Threading;

namespace LogSimulator.State
{
    public class NoAlarmState : IState
    {
        public string Description => Helper.Constants.StateDescription.NoAlarm;

        private ILogService _logService;
        private IAppSettings _appSettings;
        private Random _random;

        public void Simulate(IAppSettings appSettings, ILogService logService)
        {
            _appSettings = appSettings;
            _logService = logService;
            _random = new Random();

            var privateMethods = typeof(NoAlarmState).GetMethods(BindingFlags.NonPublic | BindingFlags.Instance);
            var methodNumber = _random.Next(privateMethods.Length - 2);
            privateMethods[methodNumber].Invoke(this, new object[] { });
        }

        private void TicketReservationState()
        {
            var flightNo = _random.Next(1, 10000);
            var log1 = _logService.GetLog($"Tickets for flight '{flightNo}' successfully retrieved", LogCategory.APP);
            var log2 = _logService.GetLog($"User with username '{_appSettings.Username}' buys bussines ticket for flight '{flightNo}'", LogCategory.APP);
            var log3 = _logService.GetLog($"Transaction successfully passed for user with username '{_appSettings.Username}' for flight '{flightNo}'", LogCategory.APP);
            _logService.WriteLogToFile(_appSettings.OtherLogsFolderPath, log1);
            Thread.Sleep(3000);
            _logService.WriteLogToFile(_appSettings.OtherLogsFolderPath, log2);
            Thread.Sleep(1000);
            _logService.WriteLogToFile(_appSettings.OtherLogsFolderPath, log3);
        }

        private void UserSendsQuestion()
        {
            var log1 = _logService.GetLog($"User with username '{_appSettings.Username}' sends question", LogCategory.APP);
            var log2 = _logService.GetLog($"Message successfully sent on email ask@megatravel.com", LogCategory.APP);
            _logService.WriteLogToFile(_appSettings.OtherLogsFolderPath, log1);
            Thread.Sleep(1000);
            _logService.WriteLogToFile(_appSettings.OtherLogsFolderPath, log2);
        }

        private void UserSearchesFlights()
        {
            var log1 = _logService.GetLog($"User with username '{_appSettings.Username}' chose fly from 'london' to 'moscow'", LogCategory.APP);
            var log2 = _logService.GetLog($"User with username '{_appSettings.Username}' filters results with params '< 300$' and 'discount'", LogCategory.APP);
            _logService.WriteLogToFile(_appSettings.OtherLogsFolderPath, log1);
            Thread.Sleep(2500);
            _logService.WriteLogToFile(_appSettings.OtherLogsFolderPath, log2);
        }
    }
}
