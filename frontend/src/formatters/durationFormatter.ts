import {getLocalePluralCase} from "@angular/common";

export function formatDuration(durationSeconds: number) {
  const hours = Math.floor(durationSeconds / 3600);
  const remainingSeconds = durationSeconds % 3600;
  const minutes = Math.floor(remainingSeconds / 60);

  // Pad minutes and seconds with leading zeros if they are less than 10
  const paddedMinutes = minutes < 10? `0${minutes}` : minutes.toString();
  // Construct the formatted string

  let minutesText: string | null = minutes > 0
    ? `${minutes}m`
    : null;

  let hoursText: string | null = hours > 0
    ? `${hours}h`
    : null;

  const resText = [hoursText, minutesText];

  return resText.join(" ");
}
