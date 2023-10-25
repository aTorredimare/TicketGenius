import { CircularProgressbar, buildStyles } from 'react-circular-progressbar';
import 'react-circular-progressbar/dist/styles.css';


const StatsProgressBar = (props) => {
    const {ratio, color} = props
    return (
        <CircularProgressbar
            value={ratio}
            text={ratio + '%'}
            strokeWidth={14}
            styles={buildStyles({
                rotation: -0.25,
                textColor: `${color}`,
                pathColor: `${color}`,
                trailColor: 'inherit',
                textSize: '16px',
            })} />
    )
}

export default StatsProgressBar