import echarts from 'echarts';

function mapToSerie(queryTimes, queriesMap) {
  return {
    name: queriesMap[queryTimes.query],
    type: 'line',
    data: queryTimes.times.map(x => ({
      value: Number(x.avgTime).toFixed(3),
      symbolSize: Math.min(x.stdDeviation / 10, 45) + 5,
      symbol: 'circle',
      stdDeviation: x.stdDeviation,
      repetitions: x.repetitions,
      executionId: x.executionId,
    })),
    smooth: true,
    emphasis: { label: { show: false }, itemStyle: { color: 'yellow' } },
    showAllSymbol: true,
    tooltip: {
      formatter: args => `
        query: ${args.seriesName}
        <br> avgTime: ${Number(args.data.value).toFixed(3)} ms 
        <br> stdDeviation: ${Number(args.data.stdDeviation).toFixed(3)} ms
        <br> repetitions: ${args.data.repetitions}`,
    },
  };
}

function createChart(htmlComponent, queriesTimes, queriesMap) {
  const overviewChart = echarts.init(htmlComponent);
  // specify chart configuration item and data
  const option = {
    tooltip: {
      show: true,
      trigger: 'item',
    },
    legend: {
      type: 'scroll',
      orient: 'vertical',
      right: 10,
      top: 20,
      bottom: 20,
      data: Object.values(queriesMap).sort(),
      tooltip: {
        show: true,
        showDelay: 500,
        triggerOn: 'mousemove',
        formatter: args => Object.keys(queriesMap).filter(
          x => queriesMap[x] === args.name,
        ),
      },
    },
    calculable: true,
    xAxis: [
      {
        type: 'category',
        boundaryGap: false,
        data: queriesTimes[0].times.map(x => ({
          value: x.commit.substring(0, 15),
          commit: x.commit,
        })),
        triggerEvent: true,
      },
    ],
    yAxis: [
      {
        type: 'value',
        axisLabel: {
          formatter: '{value} ms',
        },
      },
    ],
    series: queriesTimes.map(queryTimes => mapToSerie(queryTimes, queriesMap)),
    dataZoom: [
      {
        type: 'inside',
        zoomOnMouseWheel: 'ctrl',
        filterMode: 'none',
        orient: 'vertical',
      },
    ],
  };
  overviewChart.setOption(option);
  return overviewChart;
}

export default { createChart };
