using LogSimulator.Helper;
using LogSimulator.Service.Interface;
using System;

namespace LogSimulator.Service
{
    public class ViewService : IViewService
    {
        public void ShowHeader()
        {
            Console.WriteLine("*****************************");
            Console.WriteLine("** Welcome to LogSimulator **");
            Console.WriteLine("*****************************");
        }

        public void ShowMainMenu()
        {
            Console.WriteLine();
            Console.WriteLine();
            Console.WriteLine("Choose an option:");
            Console.WriteLine("1) Random alarm states");
            Console.WriteLine("2) Manual alarm states");
            Console.WriteLine("3) Exit");
            Console.Write("Option number >> ");
        }

        public char ReadKey()
        {
            var key = Console.ReadKey();
            return key.KeyChar;
        }

        public void PrintRandomServiceStarted()
        {
            Console.WriteLine("\nRandom service started...");
        }

        public void PrintPressAnyKeyToStopService()
        {
            Console.WriteLine("\nPress any key to stop service...");
        }
        
        public void PrintInvalidInput()
        {
            Console.WriteLine("\nInvalid input!");
        }

        public void ShowManualMenu()
        {
            Console.WriteLine();
            Console.WriteLine();
            Console.WriteLine("Choose an option:");
            Console.WriteLine($"1) {Constants.StateDescription.NoAlarm}");
            Console.WriteLine($"2) {Constants.StateDescription.LogError}");
            Console.WriteLine("3) << Back");
            Console.Write("Option number >> ");
        }

        public void PrintEnteredState(string stateDescription)
        {
            Console.WriteLine($"\nEntered state: {stateDescription}");
        }
    }
}
