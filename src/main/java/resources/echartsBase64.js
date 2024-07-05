// Function to create and return base64 string of an EChart
function getEChartBase64() {
  // Create a temporary DOM element to render the chart
  var chartContainer = document.createElement("div");
  chartContainer.style.width = "@width" + "px";
  chartContainer.style.height = "@height" + "px";
  document.body.appendChild(chartContainer);

  // Initialize EChart
  var chart = echarts.init(chartContainer);

  // Parse the injected data
  // var option = JSON.parse("@visualData");
  var option = {
    title: {
      text: "ECharts Getting Started Example",
    },
    tooltip: {},
    legend: {
      data: ["sales"],
    },
    xAxis: {
      data: ["Shirts", "Cardigans", "Chiffon", "Pants", "Heels", "Socks"],
    },
    yAxis: {},
    series: [
      {
        name: "sales",
        type: "bar",
        data: [5, 20, 36, 10, 10, 20],
      },
    ],
  };
  option.animation = false;
  // Combine model and visual data to create the chart option
  // var option = createChartOption(visualData);

  // Set the option and render the chart
  chart.setOption(option);

  // Get the base64 string of the chart
  var base64 = chart.getDataURL({
    type: "png",
    pixelRatio: 2, // Adjust for higher resolution if needed
    backgroundColor: "#fff", // Set background color if needed
  });
  const len = "data:image/png;base64,".length;
  base64 = base64.substring(len);
  return base64;

  // Clean up: dispose of the chart and remove the temporary container
  // chart.dispose();
  // document.body.removeChild(chartContainer);
}

// Function to be called from outside to get the base64 string

getEChartBase64();

// function createChart() {
//   var chartContainer = document.createElement("div");
//   chartContainer.style.width = "@width" + "px";
//   chartContainer.style.height = "@height" + "px";
//   document.body.appendChild(chartContainer);
//   // Initialize the echarts instance
//   var myChart = echarts.init(chartContainer);
//
//   // Specify the configuration items and data for the chart
//   var option = {
//     title: {
//       text: "ECharts Getting Started Example",
//     },
//     tooltip: {},
//     legend: {
//       data: ["sales"],
//     },
//     xAxis: {
//       data: ["Shirts", "Cardigans", "Chiffon", "Pants", "Heels", "Socks"],
//     },
//     yAxis: {},
//     series: [
//       {
//         name: "sales",
//         type: "bar",
//         data: [5, 20, 36, 10, 10, 20],
//       },
//     ],
//   };
//
//   // Set the chart options and data
//   myChart.setOption(option);
// }
//
// createChart();
