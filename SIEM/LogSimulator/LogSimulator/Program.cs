using LogSimulator.Service;
using Microsoft.Extensions.Configuration;
using System.IO;
using System.Threading.Tasks;

namespace LogSimulator
{
    class Program
    {
        static void Main(string[] args)
        {
            var configurationRoot = GetConfigurationRoot();
            var mainService = new MasterService(configurationRoot);
            mainService.Start();
        }

        private static IConfigurationRoot GetConfigurationRoot()
        {
            var builder = new ConfigurationBuilder()
                        .SetBasePath(Directory.GetCurrentDirectory())
                        .AddJsonFile("appsettings.json", optional: true, reloadOnChange: true);

            return builder.Build();
        }
    }
}
