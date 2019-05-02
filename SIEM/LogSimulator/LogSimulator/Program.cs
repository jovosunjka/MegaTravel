using LogSimulator.Service;

namespace LogSimulator
{
    class Program
    {
        static void Main(string[] args)
        {
            var mainService = new MasterService();
            mainService.Start();
        }
    }
}
